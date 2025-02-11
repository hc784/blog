package blog.post.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import blog.post.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 최상위(부모가 없는) 카테고리 조회
    List<Category> findByParentIsNull();

    // 특정 부모 카테고리의 자식만 조회
    List<Category> findByParent(Category parent);
    
    List<Category> findByParentIsNullOrderByCategoryOrderAsc();
    
    
    
    
    
    // 해당 블로그의 최상위 카테고리(부모가 없는)를 정렬 순서대로 조회
    List<Category> findByBlogIdAndParentIsNullOrderByCategoryOrderAsc(Long blogId);
    
    // 해당 블로그에서 특정 부모 카테고리에 속한 하위 카테고리 조회
    List<Category> findByBlogIdAndParent(Long blogId, Category parent);
    
    // 특정 카테고리 ID와 블로그 ID를 기준으로 조회 (존재 여부 확인용)
    Optional<Category> findByIdAndBlogId(Long id, Long blogId);
    
    // 해당 블로그의 모든 카테고리 조회
    List<Category> findByBlogId(Long blogId);
}
