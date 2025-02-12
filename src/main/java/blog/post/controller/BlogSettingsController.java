package blog.post.controller;

import blog.post.model.Blog;
import blog.post.service.BlogService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blogs/{blogId}/settings")
public class BlogSettingsController {

    private final BlogService blogService;

    public BlogSettingsController(BlogService blogService) {
        this.blogService = blogService;
    }

    // 블로그 설정 페이지 표시 (GET)
    @GetMapping
    public String showSettings(@PathVariable Long blogId, Model model) {
        Blog blog = blogService.getBlogById(blogId);
        model.addAttribute("blog", blog);
        return "blog/settings";
    }

    // 블로그 설정 업데이트 (POST)
    @PostMapping
    public String updateSettings(@PathVariable Long blogId,
                                 @RequestParam String title,
                                 @RequestParam String description,
                                 Model model) {
        blogService.updateBlogSettings(blogId, title, description);
        return "redirect:/blogs/" + blogId + "/settings";
    }
}
