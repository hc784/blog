package blog.security.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.security.model.User;
import blog.security.repository.UserRepository;
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BlogRepository blogRepository;
    
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; 
    }
    
	/*
	 * @GetMapping("/register") public String registerPage() { return "auth"; //
	 * 회원가입 페이지 }
	 */

    @PostMapping("/register")
    public String register( @RequestParam("username") String username,
    	    @RequestParam("password") String password,
    	    @RequestParam("email") String email,
    	    @RequestParam("confirmPassword") String confirmPassword,
    	    @RequestParam("nickname") String nickname,  Model model) {
        // 입력값 검증
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Username cannot be empty");
            return "register";
        }
        if (password == null || password.isBlank()) {
            model.addAttribute("error", "Password cannot be empty");
            return "register";
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            model.addAttribute("error", "Invalid email address");
            return "register";
        }

        // 사용자 중복 체크
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 저장
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole("USER");
        user.setActive(true); // 활성화 상태
        
        userRepository.save(user);
        Blog blog = new Blog("null",user);
        user.setBlog(blog); // 연관 관계 설정
        blogRepository.save(blog);

        
        return "redirect:/login"; // 회원가입 후 로그인 페이지로 이동
    }
}
