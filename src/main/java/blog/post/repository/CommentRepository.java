package blog.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import blog.post.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPostIdAndParentIsNull(Long postId);
	
	   
	    @Query(value = "SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.post.id = :postId AND c.parent IS NULL",
	           countQuery = "SELECT count(c) FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL")
	    Page<Comment> findParentCommentsByPostId(@Param("postId") Long postId, Pageable pageable);


	    @Query("SELECT r FROM Comment r LEFT JOIN FETCH r.user WHERE r.parent.id IN :parentIds")
	    List<Comment> findRepliesByParentIds(@Param("parentIds") List<Long> parentIds);
	

}
