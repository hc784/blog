package blog.post.dto;

import lombok.Getter;
import lombok.Setter;

// 댓글 생성을 위한 요청 DTO
@Getter
@Setter
public class CommentRequestDto {
    private Long postId;
    private String content;
    private String anonymousAuthor;
    private String anonymousPassword;
    private Long userId;
    private Long parentId; // 답글인 경우 부모 댓글의 ID
}
