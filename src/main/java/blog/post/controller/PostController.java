package blog.post.controller;

import blog.post.dto.PostDto;
import blog.post.model.Category;
import blog.post.model.Post;
import blog.post.service.CategoryService;
import blog.post.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController { 

    private final PostService postService;
    private final CategoryService categoryService;
    public PostController(PostService postService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
    }

    
    // 글 작성 페이지를 보여줌 (뷰 렌더링)
    @GetMapping("/create")
    public String showCreatePostForm(Model model) {
    	List<Category> categories = categoryService.getTopLevelCategories();
    	model.addAttribute("categories", categories);
    	model.addAttribute("post", new Post());
        return "post/write";  // post-form.html로 이동
    }

    // 글 작성 처리
    @PostMapping({"/create", "/edit/{id}"})
    public String createPost(PostDto postDTO, Model model, RedirectAttributes redirectAttributes) {
        Post post = postService.createPost(postDTO.getTitle(), postDTO.getContent(), postDTO.getCategoryId());
        model.addAttribute("post", post);
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다!");
        return "redirect:/posts/" + post.getId();  // 글 상세 페이지로 리다이렉트
    }

//    // 전체 게시글 목록 페이지
//    @GetMapping
//    public String getAllPosts(Model model, @RequestParam(value = "category_id", required = false) Long categoryId) {
//        List<Post> posts;
//        if (categoryId == null) {
//            posts = postService.getAllPosts();
//        } else {
//            posts = postService.findByCategoryIdOrChild(categoryId);
//        }
//        List<Category> categories = categoryService.getTopLevelCategories();
//        model.addAttribute("categories", categories);
//        model.addAttribute("posts", posts);
//        return "post/category";
//    }

    @GetMapping
    public String getAllPosts(Model model,
    		@RequestParam(value = "category_id", required = false) Long categoryId,
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    	Page<Post> postPage;
    	   
        if (categoryId == null) {
            postPage = postService.getPaginatedPosts(page, size);
        } else {
            postPage = postService.getPaginatedPostsByCategory(categoryId, page, size);
        }

        List<Category> categories = categoryService.getTopLevelCategories();
        
     // 블록 페이징 계산 (5개씩 출력)
        int blockSize = 5;
        int startPage = Math.max(0, (page / blockSize) * blockSize);
        int endPage = Math.min(startPage + blockSize - 1, postPage.getTotalPages() - 1);
        
        model.addAttribute("categories", categories);
        model.addAttribute("posts", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "post/category";  // post-list.html 렌더링
    }
    // 특정 게시글 상세 페이지
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post/detail";  // post-detail.html 렌더링
    }
    
    // 게시글 수정 페이지를 보여줌
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
    	List<Category> categories = categoryService.getTopLevelCategories();
    	model.addAttribute("categories", categories);
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post/edit";  // post-edit.html 렌더링
    }

    // 게시글 수정 처리
	/*
	 * @PostMapping("/edit/{id}") public String updatePost(@PathVariable Long
	 * id, @RequestParam String title, @RequestParam String content,
	 * RedirectAttributes redirectAttributes) { postService.updatePost(id, title,
	 * content); redirectAttributes.addFlashAttribute("message",
	 * "게시글이 성공적으로 수정되었습니다!"); return "redirect:/posts/" + id; // 수정 후 해당 게시글 상세
	 * 페이지로 이동 }
	 */
    
    // 게시글 삭제 처리 후 목록 페이지로 리다이렉트
    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";  // 목록 페이지로 리다이렉트
    }
}
