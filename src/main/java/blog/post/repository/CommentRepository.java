package blog.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.post.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
