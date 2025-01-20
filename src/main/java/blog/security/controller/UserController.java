package blog.security.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import blog.security.model.User;
import blog.security.security.PrincipalDetails;

@Controller
public class UserController {

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            model.addAttribute("username", principalDetails.getName());
            model.addAttribute("email", principalDetails.getEmail());

        return "profile";
    }
}
