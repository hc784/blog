package blog.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // 모든 요청 허용
                )
                .formLogin(login -> login
                    .loginPage("/login") // 사용자 정의 로그인 페이지
                    .defaultSuccessUrl("/loginMain") // 로그인 성공 후 이동 경로
                    .permitAll() // 로그인 페이지 누구나 접근 가능
                )
                .logout(logout -> logout
                    .logoutUrl("/logout") // 로그아웃 처리 URL
                    .logoutSuccessUrl("/loginMain") // 로그아웃 성공 후 이동 경로
                    .permitAll()
                );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 사용
    }
    
    
}