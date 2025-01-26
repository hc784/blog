package blog.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import blog.post.dto.CategoryRequest;
import blog.post.model.Category;
import blog.post.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 최상위 카테고리 조회
//    @GetMapping("/top-level")
//    public String getTopLevelCategories(Model model) {
//        List<Category> categories = categoryService.getTopLevelCategories();
//        model.addAttribute("categories", categories);
//        return "categories/topLevel";  // Thymeleaf 등에서 사용할 뷰 템플릿 경로
//    }
//
//    // 특정 부모 카테고리의 자식 조회
//    @GetMapping("/subcategories/{parentId}")
//    public String getSubCategories(@PathVariable Long parentId, Model model) {
//        List<Category> subCategories = categoryService.getSubCategories(parentId);
//        model.addAttribute("subCategories", subCategories);
//        return "categories/subCategories";  // Thymeleaf 뷰 템플릿 경로
//    }
    
    @GetMapping("/categories")
    public String showCategoryList(Model model) {
        List<Category> categories = categoryService.getTopLevelCategories();
        model.addAttribute("categories", categories);
        return "categories/list";  // Thymeleaf 템플릿 파일 경로
    }
    
    @GetMapping("/setting")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getTopLevelCategories();
        model.addAttribute("categories", categories);
        return "categories/setting";
    }
    
    @PostMapping("/update")
    public ResponseEntity<String> updateCategories(@RequestBody CategoryRequest request) {
    	try {
    		categoryService.updateCategories(request);
    		return ResponseEntity.ok("카테고리가 성공적으로 저장되었습니다.");
    	}
    	catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
        
        
    }
    
}
