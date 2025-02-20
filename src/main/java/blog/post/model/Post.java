package blog.post.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import blog.security.model.User;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Long blogId;  // 소속된 블로그

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;  // 조회수 추가

    private boolean published = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // 2단계 카테고리 연관 추가
    
    private String imageUrl;
}
