package blog.post.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import blog.post.dto.CommentDto;
import blog.post.dto.CommentRequestDto;
import blog.post.model.Comment;
import blog.post.service.CommentService;
import blog.security.model.User;
import blog.security.security.PrincipalDetails;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails) { 
        
        Long userId = (principalDetails != null) ? principalDetails.getUser().getId() : null;

        CommentDto createdComment = commentService.createComment(requestDto);
        return ResponseEntity.ok(createdComment);
    }
    
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentDto>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CommentDto> comments = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok(comments);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestBody(required = false) Map<String, String> payload, // 익명 댓글 비밀번호를 받기 위함
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
            
        String password = (payload != null) ? payload.get("password") : null;
        User user = (principalDetails != null) ? principalDetails.getUser() : null;
        
        try {
	        commentService.deleteComment(commentId, password, user);
	        return ResponseEntity.ok().build();
        }
        
        catch (IllegalArgumentException e) {
            // 권한이 없는 경우 Forbidden(403) 또는 Unauthorized(401) 반환
            return ResponseEntity.status(403).build(); 
        }
    }
}
