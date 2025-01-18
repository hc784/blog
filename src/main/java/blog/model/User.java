package blog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String role;

    // 추가적인 OAuth 필드
    private String provider;    // OAuth 제공자 (e.g., google, naver)
    private String providerId;  // OAuth 제공자에서 사용하는 사용자 ID

    // 추가적인 비즈니스 필드
    private String email;
    private boolean active;
}
