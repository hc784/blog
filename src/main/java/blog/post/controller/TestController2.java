package blog.post.controller;

import blog.post.model.Post;
import blog.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController2 { 

    private final PostService postService;

    public TestController2(PostService postService) {
        this.postService = postService;
    }

    
    // 글 작성 페이지를 보여줌 (뷰 렌더링)
    @GetMapping("/header")
    public String showCreatePostForm() {
        return "partials/header";  // post-form.html로 이동
    }
    
    @GetMapping("/starter-page")
    public String stater() {
        return "starter-page";  // post-form.html로 이동
    }
    
    @GetMapping("/category")
    public String stater11() {
        return "category";  // post-form.html로 이동
    }
    
    @GetMapping("/write")
    public String stater111() {
        return "post/write";  // post-form.html로 이동
    }
}
