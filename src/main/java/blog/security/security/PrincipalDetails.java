package blog.security.security;

import blog.post.model.Blog;
import blog.security.model.User;
import blog.security.oauth.OAuth2UserInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@Setter
public class PrincipalDetails implements UserDetails, OAuth2User {
	
    private static final long serialVersionUID = 1L;  // 직렬화 ID 추가
    
    private User user; // User 엔티티
    private OAuth2UserInfo oAuth2UserInfo; // OAuth2 사용자 정보 인터페이스

    // 생성자 (기본 로그인)
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 생성자 (OAuth2 로그인)
    public PrincipalDetails(User user, OAuth2UserInfo oAuth2UserInfo) {
        this.user = user;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }
    
    public Blog getBlog() {
    	return user.getBlog();
    }
    public Long getBlogId() {
    	return user.getBlog().getId();
    }
    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + user.getRole());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getName(); // 제공자의 사용자 이름 반환
    }

    public String getEmail() {
        return oAuth2UserInfo.getEmail();
    }

    public String getProvider() {
        return oAuth2UserInfo.getProvider();
    }

    public String getProviderId() {
        return oAuth2UserInfo.getProviderId();
    }
}
