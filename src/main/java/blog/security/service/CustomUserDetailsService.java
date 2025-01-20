package blog.security.service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import blog.security.model.User;
import blog.security.repository.UserRepository;
import blog.security.security.PrincipalDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional 사용하여 null 처리
       User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
       PrincipalDetails principalDetails = new PrincipalDetails(user);
        // 활성화 여부 체크
        if (!principalDetails.isEnabled()) {
            throw new IllegalStateException("Account is disabled");
        }

        return principalDetails; // CustomUser가 UserDetails를 구현하므로 바로 반환 가능
    }
}