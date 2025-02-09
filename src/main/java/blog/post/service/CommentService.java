package blog.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.post.model.Comment;
import blog.post.model.Post;
import blog.post.repository.CommentRepository;
import blog.post.repository.PostRepository;
import blog.security.model.User;
import blog.security.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Comment createComment(Long postId, String content, String anonymousAuthor,
                                 String anonymousPassword, Long userId, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다: " + postId));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setAnonymousAuthor(anonymousAuthor);
        if(anonymousPassword != null) {
            comment.setAnonymousPassword(passwordEncoder.encode(anonymousPassword));
        }
        // 만약 부모 댓글 ID가 전달되면 답글로 처리
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다: " + parentId));
            comment.setParent(parentComment);
        }
        // userId에 따른 로그인 사용자 처리도 추가할 수 있음

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Transactional
    public boolean deleteComment(Long commentId, String anonymousPassword) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));

        if (comment.isAnonymous()) {
            if (anonymousPassword == null || !passwordEncoder.matches(anonymousPassword, comment.getAnonymousPassword())) {
                return false;
            }
        }
        commentRepository.delete(comment);
        return true;
    }
}