package blog.post.service;
import org.springframework.stereotype.Service;

import blog.post.dto.CategoryDto;
import blog.post.dto.CategoryRequest;
import blog.post.model.Category;
import blog.post.repository.CategoryRepository;
import blog.post.repository.PostRepository;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    
    public CategoryService(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository= postRepository;
    }

    // 최상위 카테고리(부모 없음) 조회
    public List<Category> getTopLevelCategories(Long blogId) {
        return categoryRepository.findByBlogIdAndParentIsNullOrderByCategoryOrderAsc(blogId);
    }

    // 특정 카테고리의 자식만 가져오기
    public List<Category> getSubCategories(Long blogId, Long parentId) {
        Category parent = categoryRepository.findByIdAndBlogId(parentId, blogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        return categoryRepository.findByBlogIdAndParent(blogId, parent);
    }
    
    // 해당 블로그의 모든 카테고리 조회
    public List<Category> getAllCategories(Long blogId) {
        return categoryRepository.findByBlogId(blogId);
    }
    
    public List<Category> getCategoriesForSidebar(Long blogId) {
        return categoryRepository.findParentCategoriesWithChildren(blogId);
    }

    @Transactional
    public void updateCategories(Long blogId, CategoryRequest request) {
        Map<String, Long> newIdAndIdMapping = new HashMap<>();
        for (CategoryDto dto : request.getCategories()) {
            if (dto.getId().startsWith("new-")) {
                // 새로운 카테고리 저장
                Category parentCategory = null;
                if (dto.getParent() != null && !dto.getParent().isEmpty()) {
                    if (dto.getParent().startsWith("new-")) {
                        parentCategory = categoryRepository.findById(newIdAndIdMapping.get(dto.getParent())).orElse(null);
                    } else {
                        parentCategory = categoryRepository.findByIdAndBlogId(Long.parseLong(dto.getParent()), blogId).orElse(null);
                    }
                }
                
                Category newCategory = new Category(dto.getName(), dto.getOrder(), parentCategory);
                newCategory.setBlogId(blogId); // 블로그 ID 설정
                Long savedId = categoryRepository.save(newCategory).getId();
                newIdAndIdMapping.put(dto.getId(), savedId);
                
            } else {
                // 기존 카테고리 업데이트
                Optional<Category> existingCategory = categoryRepository.findByIdAndBlogId(Long.parseLong(dto.getId()), blogId);
                if (existingCategory.isPresent()) {
                    Category category = existingCategory.get();
                    category.setName(dto.getName());
                    category.setCategoryOrder(dto.getOrder());
                    if (dto.getParent() != null && !dto.getParent().isEmpty()) {
                        category.setParent(categoryRepository.findByIdAndBlogId(Long.parseLong(dto.getParent()), blogId)
                                .orElse(null));
                    } else {
                        category.setParent(null);
                    }
                    categoryRepository.save(category);
                }
            }
        }
        
        // 카테고리 삭제 처리 (해당 블로그의 게시글이 존재하는지 확인)
        if (request.getDeletedCategories() != null && !request.getDeletedCategories().isEmpty()) {
            if (postRepository.countByCategoryIdInAndBlogId(request.getDeletedCategories(), blogId) > 0) {
                throw new IllegalStateException("삭제한 카테고리에 작성된 게시글이 존재합니다. 게시글을 삭제하거나 이동해주세요.");
            } else {
                categoryRepository.deleteAllById(request.getDeletedCategories());
            }
        }
    }
}
