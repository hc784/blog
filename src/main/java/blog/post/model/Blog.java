package blog.post.model;

import jakarta.persistence.*;
import blog.security.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private String title;  // 블로그 이름 (유일해야 함)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    @NonNull
    private User owner;  // 블로그 주인 (각 블로그는 하나의 주인을 가짐)

}
