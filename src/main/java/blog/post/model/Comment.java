package blog.post.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import blog.security.model.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private Post post;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // 로그인한 사용자 (익명일 경우 null)

    private String anonymousAuthor; // 익명 작성자 닉네임

    private String anonymousPassword; // 익명 작성자 비밀번호 (암호화 저장)

    
    @Column(nullable = false, length = 500)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    public boolean isAnonymous() {
        return user == null;
    }
    
    // 부모 댓글 (답글인 경우 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Comment parent;
    
    // 자식 댓글 (답글 목록)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 순환 참조 방지: 부모-자식 관계에서 자식 목록 직렬화
    private List<Comment> replies = new ArrayList<>();
    
}
