package blog.post.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import blog.post.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 최상위(부모가 없는) 카테고리 조회
    List<Category> findByParentIsNull();

    // 특정 부모 카테고리의 자식만 조회
    List<Category> findByParent(Category parent);
    
    List<Category> findByParentIsNullOrderByCategoryOrderAsc();
}
