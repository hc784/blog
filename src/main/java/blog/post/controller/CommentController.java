package blog.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import blog.post.model.Comment;
import blog.post.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam Long postId,
            @RequestParam String content,
            @RequestParam(required = false) String anonymousAuthor,
            @RequestParam(required = false) String anonymousPassword,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long parentId) {  // 부모 댓글 ID (답글인 경우)
        
        Comment comment = commentService.createComment(postId, content, anonymousAuthor, anonymousPassword, userId, parentId);
        return ResponseEntity.status(201).body(comment);  // 생성 시 201 Created
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) String anonymousPassword) {

        commentService.deleteComment(commentId, anonymousPassword);
        return ResponseEntity.noContent().build();
    }
}
