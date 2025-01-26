package blog.post.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
	
    // 기본 생성자
    public Category() {}

    // 생성자
    public Category(String name, int categoryOrder, Category parent) {
        this.name = name;
        this.categoryOrder = categoryOrder;
        this.parent = parent;
    }

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 100)
    private String name;
    private int categoryOrder;
    // 부모 카테고리 (최상위 카테고리는 null, 서브 카테고리는 부모 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 부모 카테고리 하위에 여러 자식 카테고리를 둘 수 있음
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("categoryOrder ASC")
    private List<Category> children;

    public boolean isParentCategory() {
        return this.parent == null; // 부모가 없으면 최상위
    }
    
} 
