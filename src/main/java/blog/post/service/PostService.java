package blog.post.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.post.model.Category;
import blog.post.model.Post;
import blog.post.repository.CategoryRepository;
import blog.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class PostService {
    
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public Post createPost(String title, String content, Long categoryId) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId)));
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    
    public Page<Post> getPaginatedPosts(int page, int size) {
    	int maxSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page - 1, maxSize, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
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
    
    
    public List<Post> findByCategoryId(Long categoryId) {
        return postRepository.findByCategoryId(categoryId);
    }
    public List<Post> findByCategoryIdOrChild(Long categoryId) {
        return postRepository.findPostsByCategoryOrChildren(categoryId);
    }
    
}
