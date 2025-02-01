package blog.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import blog.post.model.Post;


public interface PostRepository extends JpaRepository<Post, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(Long postId);
    
    Page<Post> findAll(Pageable pageable);
    
    List<Post> findByCategoryId(Long categoryId);
    
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId OR p.category.parent.id = :categoryId")
    List<Post> findPostsByCategoryOrChildren(Long categoryId);
    
    // 특정 카테고리 또는 자식 카테고리 포함한 게시글 페이징 조회
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId OR p.category.parent.id = :categoryId")
    Page<Post> findPostsByCategoryOrChildren(Long categoryId, Pageable pageable);
    
    int countByCategoryIdIn(List<Long> categoryIds);
}
