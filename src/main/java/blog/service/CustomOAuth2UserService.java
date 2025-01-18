package blog.service;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import blog.model.User;
import blog.repository.UserRepository;
import jakarta.servlet.http.HttpSession;



@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	 private final UserRepository userRepository;

	    public CustomOAuth2UserService(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver 등
        String providerId = oAuth2User.getName(); // 제공자의 고유 ID
        String email = oAuth2User.getAttributes().get("email").toString(); // 사용자 이메일
        String name = oAuth2User.getAttributes().get("name").toString();  // 사용자 이름

        // DB에 사용자 저장 또는 업데이트
        User user = saveOrUpdateUser(provider, providerId, email, name);

        return oAuth2User;
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

            return userRepository.save(newUser);
        }
    }
}

