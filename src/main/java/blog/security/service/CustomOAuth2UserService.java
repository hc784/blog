package blog.security.service;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.security.model.User;
import blog.security.oauth.GoogleUserInfo;
import blog.security.oauth.KakaoUserInfo;
import blog.security.oauth.NaverUserInfo;
import blog.security.oauth.OAuth2UserInfo;
import blog.security.repository.UserRepository;
import blog.security.security.PrincipalDetails;
import jakarta.servlet.http.HttpSession;



@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

    private final BlogRepository blogRepository;

    public CustomOAuth2UserService(UserRepository userRepository, BlogRepository blogRepository) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver 등
        System.out.println(oAuth2User.getAttributes());
        System.out.println(provider);
        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
        	oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
        	oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else {
            System.out.println("로그인 실패");
        }
        // DB에 사용자 저장 또는 업데이트
        User user = saveOrUpdateUser(provider, oAuth2UserInfo.getProviderId(), oAuth2UserInfo.getEmail(),oAuth2UserInfo.getName());

        return new PrincipalDetails(user,oAuth2UserInfo);
    }

    private User saveOrUpdateUser(String provider, String providerId, String email, String name) {
        Optional<User> optionalUser = userRepository.findByProviderAndProviderId(provider, providerId);

        if (optionalUser.isPresent()) {
            return optionalUser.get(); // 기존 사용자 반환
        } else {
            // 새로운 사용자 저장
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setRole("USER");
            newUser.setProvider(provider);
            newUser.setProviderId(providerId);
            newUser.setActive(true);
            newUser.setNickname(email);
            
            Blog blog = new Blog("null",newUser);
            newUser.setBlog(blog); // 연관 관계 설정
            blogRepository.save(blog);

            return userRepository.save(newUser);
        }
    }
}

