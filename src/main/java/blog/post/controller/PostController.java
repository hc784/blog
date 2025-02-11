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
@RequestMapping("/blogs/{blogId}/posts")
public class PostController { 

    private final PostService postService;
    private final CategoryService categoryService;
    public PostController(PostService postService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
    }

    
    // 글 작성 페이지 (뷰 렌더링)
    @GetMapping("/create")
    public String showCreatePostForm(@PathVariable Long blogId, Model model) {
        List<Category> categories = categoryService.getTopLevelCategories(blogId);
        model.addAttribute("categories", categories);
        model.addAttribute("post", new Post());
        model.addAttribute("blogId", blogId); // 뷰에서 blogId 활용 가능
        return "post/write";  // post/write.html로 이동
    }
    // 글 작성 처리
    @PostMapping({"/create", "/edit/{id}"})
    public String createPost(@PathVariable Long blogId, PostDto postDTO, 
                             Model model, RedirectAttributes redirectAttributes) {
        // PostService의 createPost 메서드에 blogId를 전달하도록 수정
        Post post = postService.createPost(blogId, postDTO.getTitle(), postDTO.getContent(), postDTO.getCategoryId());
        model.addAttribute("post", post);
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다!");
        return "redirect:/blogs/" + blogId + "/posts/" + post.getId();  // 글 상세 페이지로 리다이렉트
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
    public String getAllPosts(@PathVariable Long blogId, Model model,
                              @RequestParam(value = "category_id", required = false) Long categoryId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        Page<Post> postPage;
        if (categoryId == null) {
            postPage = postService.getPaginatedPosts(blogId, page, size);
        } else {
            postPage = postService.getPaginatedPostsByCategory(blogId, categoryId, page, size);
        }
        
        List<Category> categories = categoryService.getTopLevelCategories(blogId);
        
        // 블록 페이징 계산 (예: 5개씩 출력)
        int blockSize = 5;
        int startPage = Math.max(0, (page / blockSize) * blockSize);
        int endPage = Math.min(startPage + blockSize - 1, postPage.getTotalPages() - 1);
        
        model.addAttribute("blogId", blogId);
        model.addAttribute("categories", categories);
        model.addAttribute("posts", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "post/category";  // post/category.html 렌더링
    }

    // 특정 게시글 상세 페이지
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long blogId, @PathVariable Long id, Model model) {
        Post post = postService.getPostById(blogId, id);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        return "post/detail";  // post/detail.html 렌더링
    }
    
    // 게시글 수정 페이지
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long blogId, @PathVariable Long id, Model model) {
        List<Category> categories = categoryService.getTopLevelCategories(blogId);
        model.addAttribute("categories", categories);
        Post post = postService.getPostById(blogId, id);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        return "post/edit";  // post/edit.html 렌더링
    }

    // 게시글 삭제 처리
    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable Long blogId, @PathVariable Long id) {
        postService.deletePost(blogId, id);
        return "redirect:/blogs/" + blogId + "/posts";  // 목록 페이지로 리다이렉트
    }
}