package blog.post.dto;

import blog.post.model.Comment;
import blog.security.model.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 클라이언트에게 반환할 댓글 정보 DTO
@Getter
public class CommentDto {

    private Long id;
    private String content;
    private String author; // 작성자 이름 (로그인/익명 통합)
    private LocalDateTime createdAt;
    private List<CommentDto> replies; // 답글 목록

    // Entity를 DTO로 변환하는 생성자
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        
        // 작성자 정보 설정
        if (comment.isAnonymous()) {
            this.author = comment.getAnonymousAuthor();
        } else {
            User user = comment.getUser();
            // User 객체가 null이 아니고, 닉네임이 있으면 닉네임, 없으면 사용자 이름 사용
            this.author = (user != null) ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "알 수 없는 사용자";
        }
        
        this.createdAt = comment.getCreatedAt();
        // 자식 댓글(답글)도 DTO로 변환하여 리스트에 담는다. (재귀 호출)
        this.replies = comment.getReplies().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }
}
