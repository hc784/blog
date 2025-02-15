package blog.post.controller;

import blog.post.model.Blog;
import blog.post.service.BlogService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/blogs/{blogId}/settings")
public class BlogSettingsController {

    private final BlogService blogService;

    public BlogSettingsController(BlogService blogService) {
        this.blogService = blogService;
    }

    // GET: 블로그 설정 페이지 표시
    @GetMapping
    @PreAuthorize("isAuthenticated() and principal.blogId == #blogId") 
    public String showSettings(@PathVariable Long blogId, Model model) {
        Blog blog = blogService.getBlogById(blogId);
        model.addAttribute("blog", blog);
        
        String profileImageUrl = (blog.getProfileImage() != null) 
                ? blog.getProfileImage()
                : "/assets/img/userIcon.png";

            model.addAttribute("profileImageUrl", profileImageUrl);
            
        return "blog/setting";
    }

    // POST: 블로그 설정 업데이트 (제목, 설명, 닉네임, 프로필 이미지)
    @PostMapping
    @PreAuthorize("isAuthenticated() and principal.blogId == #blogId") 
    public String updateSettings(@PathVariable Long blogId,
                                 @RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam String nickname,
                                 @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                 Model model) {
        blogService.updateBlogSettings(blogId, title, description, nickname, profileImage);
        return "redirect:/blogs/" + blogId + "/settings";
    }
}