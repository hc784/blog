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
    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIsNullOrderByCategoryOrderAsc();
    }

    // 특정 카테고리의 자식만 가져오기
    public List<Category> getSubCategories(Long parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        return categoryRepository.findByParent(parent);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();  // 모든 카테고리 조회
    }
    
    @Transactional
    public void updateCategories(CategoryRequest request) {
    	Map<String,Long> newIdAndIdMapping = new HashMap<String,Long>();
        for (CategoryDto dto : request.getCategories()) {
            if (dto.getId().startsWith("new-")) {
                // 새로운 카테고리 저장
                Category parentCategory = null;
                if (dto.getParent() != null && !dto.getParent().isEmpty()) {
                	if(dto.getParent().startsWith("new-")) {
                		parentCategory = categoryRepository.findById(newIdAndIdMapping.get(dto.getParent())).get();
                	}
                	else {
                		parentCategory = categoryRepository.findById(Long.parseLong(dto.getParent())).orElse(null);
                	}
                }
                
                Category newCategory = new Category(dto.getName(), dto.getOrder(), parentCategory);
                newIdAndIdMapping.put(dto.getId(), categoryRepository.save(newCategory).getId())  ;
                
            } else {
                // 기존 카테고리 업데이트
                Optional<Category> existingCategory = categoryRepository.findById(Long.parseLong(dto.getId()));
                if (existingCategory.isPresent()) {
                    Category category = existingCategory.get();
                    category.setName(dto.getName());
                    category.setCategoryOrder(dto.getOrder());
                    if (dto.getParent() != null && !dto.getParent().isEmpty()) {
                        category.setParent(categoryRepository.findById(Long.parseLong(dto.getParent()))
                                .orElse(null));
                    } else {
                        category.setParent(null);
                    }
                    categoryRepository.save(category);
                }
            }
        }
        
        if (request.getDeletedCategories() != null && !request.getDeletedCategories().isEmpty()) {
        	if (postRepository.countByCategoryIdIn(request.getDeletedCategories()) > 0) {
        	    throw new IllegalStateException("삭제한 카테고리에 작성된 게시글이 존재합니다. 게시글을 삭제하거나 이동해주세요.");
        	}
        	else {
            categoryRepository.deleteAllById(request.getDeletedCategories());
        	}
        }
    }
}
