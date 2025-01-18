package blog.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import blog.model.User;

@Controller
public class UserController {

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            model.addAttribute("username", oAuth2User.getAttributes().get("name"));
            model.addAttribute("email", oAuth2User.getAttributes().get("email"));
        } else {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", ((User) user).getEmail());
        }
        return "profile";
    }
}
