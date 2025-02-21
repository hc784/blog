package blog.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.post.dto.PostDto;
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

    // blogId를 포함하여 게시글 생성
 // 게시글 생성 (DTO를 매개변수로 받음)
    public Post createPost(Long blogId, PostDto postDTO) {
        Post post = new Post();
        post.setBlogId(blogId);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategory(categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + postDTO.getCategoryId())));
        return postRepository.save(post);
    }

    
    public Post createDraft(Long blogId) {
        Post post = new Post();
        post.setBlogId(blogId);
        post.setDraft(true);  // 임시 저장 상태
        return postRepository.save(post);
    }
    
    
 // 게시글 수정 처리 (필요 시 blogId 체크)
    public Post updatePost(Long blogId, Long id, PostDto postDTO) {
        Post post = getPostById(blogId, id);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategory(categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + postDTO.getCategoryId())));
        post.setDraft(false);
        return postRepository.save(post);
    }
    
    // 전체 게시글 조회 (blogId 조건 추가)
    public Page<Post> getPaginatedPosts(Long blogId, int page, int size) {
        int maxSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, maxSize, Sort.by("createdAt").descending());
        return postRepository.findByBlogId(blogId, pageable);  // blogId를 이용해 조회
    }
    
    // 특정 카테고리(및 자식 카테고리 포함) 게시글 조회 (blogId 조건 추가)
    public Page<Post> getPaginatedPostsByCategory(Long blogId, Long categoryId, int page, int size) {
        int maxSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, maxSize, Sort.by("createdAt").descending());
        // Repository에 blogId와 category 관련 조건을 함께 처리하는 메서드 필요
        return postRepository.findPostsByBlogIdAndCategoryOrChildren(blogId, categoryId, pageable);
    }
    
    // 게시글 수정 처리 (필요 시 blogId 체크)
    public void updatePost(Long blogId, Long id, String title, String content) {
        Post post = getPostById(blogId, id);
        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);
    }
    
    @Transactional
    public Post getPostById(Long blogId, Long id) {
        postRepository.incrementViewCount(id);  // 조회수 증가
        // blogId 조건을 추가하여 게시글 조회
        return postRepository.findByIdAndBlogId(id, blogId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 삭제 (blogId 조건 추가)
    public void deletePost(Long blogId, Long id) {
        Post post = getPostById(blogId, id);
        postRepository.delete(post);
    }
    
    // 🔹 제목 + 내용 하나의 검색어로 검색 (구분 없이 검색)
    public Page<Post> searchPosts(Long blogId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByBlogIdAndTitleContainingIgnoreCaseOrBlogIdAndContentContainingIgnoreCase(
                blogId, keyword, blogId, keyword, pageable);
    }
    
    public List<Post> getRecentPosts(Long blogId) {
        return postRepository.findTop5ByBlogIdOrderByCreatedAtDesc(blogId);
    }
}
