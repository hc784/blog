package blog.security.controller;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/about")
    public String sayHello() {
        return "about";
    }
}