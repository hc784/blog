package blog.post.controller;

import blog.post.dto.PostDto;
import blog.post.model.Blog;
import blog.post.model.Category;
import blog.post.model.Post;
import blog.post.service.BlogService;
import blog.post.service.CategoryService;
import blog.post.service.PostService;
import blog.security.security.PrincipalDetails;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BlogController { 

    private final PostService postService;
    private final CategoryService categoryService;
    private final BlogService blogService;
    
    public BlogController(PostService postService, CategoryService categoryService, BlogService blogService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.blogService = blogService;
    }

    
    @GetMapping("/blogs")
    public String showBlogList(Model model) {
        List<Blog> blogs = blogService.getAllBlogs();
        model.addAttribute("blogs", blogs);
        return "blog/blogMain";
    }

}