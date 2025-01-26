package blog.post.dto;

import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;  // Category ID만 전달
    private String imageUrl;
}