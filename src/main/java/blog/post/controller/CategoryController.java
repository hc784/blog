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
