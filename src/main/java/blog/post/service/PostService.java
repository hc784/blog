package blog.post.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.post.model.Post;
import blog.post.repository.PostRepository;

import java.util.List;

@Service
public class PostService {
    
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    public void updatePost(Long id, String title, String content) {
        Post post = getPostById(id);
        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);
    }
    
    @Transactional
    public Post getPostById(Long id) {
        postRepository.incrementViewCount(id);  // 조회수 증가
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
