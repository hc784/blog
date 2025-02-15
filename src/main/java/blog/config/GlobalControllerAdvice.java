package blog.config;

import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.post.service.PostService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping("/blogs/{blogId}") // 해당 URL 패턴이 있을 때만 실행
public class GlobalControllerAdvice {

    private final BlogRepository blogRepository;
    private final PostService postService;
    public GlobalControllerAdvice(BlogRepository blogRepository, PostService postService) {
        this.blogRepository = blogRepository;
        this.postService = postService;
    }

    @ModelAttribute
    public void addBlogOwnerInfoToModel(@PathVariable(required = false) Long blogId, Model model) {
        if (blogId != null) {
            Blog blog = blogRepository.findById(blogId).orElse(null);
            if (blog != null) {
                model.addAttribute("blogOwnerNickname", blog.getOwner().getNickname());
                model.addAttribute("blogDescription",blog.getDescription());
                String profileImageUrl = (blog.getProfileImage() != null) 
                        ? blog.getProfileImage()
                        : "/assets/img/userIcon.png";

                    model.addAttribute("profileImageUrl", profileImageUrl);
                    model.addAttribute("recentPosts",postService.getRecentPosts(blogId)); 
            }
        }
    }
}
