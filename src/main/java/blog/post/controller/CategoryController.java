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
@RequestMapping("/blogs/{blogId}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 특정 블로그의 카테고리 목록 조회
    @GetMapping
    public String showCategoryList(@PathVariable Long blogId, Model model) {
        List<Category> categories = categoryService.getTopLevelCategories(blogId);
        model.addAttribute("categories", categories);
        model.addAttribute("blogId", blogId);
        return "categories/list";  // Thymeleaf 템플릿 파일 경로
    }

    // 특정 블로그의 카테고리 설정 페이지
    @GetMapping("/setting")
    public String listCategories(@PathVariable Long blogId, Model model) {
        List<Category> categories = categoryService.getTopLevelCategories(blogId);
        model.addAttribute("categories", categories);
        model.addAttribute("blogId", blogId);
        return "categories/setting";
    }

    // 블로그별 카테고리 업데이트
    @PostMapping("/update")
    public ResponseEntity<String> updateCategories(@PathVariable Long blogId, @RequestBody CategoryRequest request) {
        try {
            categoryService.updateCategories(blogId, request);
            return ResponseEntity.ok("카테고리가 성공적으로 저장되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}