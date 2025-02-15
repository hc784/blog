package blog.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import blog.post.service.BlogService;
import blog.security.service.CustomOAuth2UserService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
    private final CustomOAuth2UserService customOAuth2UserService;
    private final BlogService blogService;
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, BlogService blogService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.blogService = blogService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
            	    // 글쓰기, 카테고리 설정, 프로필 설정 등 인증이 필요한 URL 설정
                    .requestMatchers("/posts/create", "/categories/setting", "/profile/**").authenticated()
                    .anyRequest().permitAll() // 모든 요청 허용
                )
                .formLogin(login -> login
                    .loginPage("/login") // 사용자 정의 로그인 페이지
                    .defaultSuccessUrl("/blogs") // 로그인 성공 후 이동 경로
                    .permitAll() // 로그인 페이지 누구나 접근 가능
                )
                .logout(logout -> logout
                    .logoutUrl("/logout") // 로그아웃 처리 URL
                    .logoutSuccessUrl("/blogs") // 로그아웃 성공 후 이동 경로
                    .permitAll()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                    .loginPage("/auth/login") // OAuth2 로그인도 동일한 로그인 페이지 사용
                    .defaultSuccessUrl("/blogs") // OAuth2 로그인 성공 후 이동 경로
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService) // 사용자 정보 처리 서비스 등록
                    )
                );
               
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 사용
    }
    

    
}