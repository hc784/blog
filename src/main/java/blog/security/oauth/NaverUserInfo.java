package blog.security.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        // Naver 응답에서 "response" 필드 안에 사용자 정보가 포함됨
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        // Naver 고유 ID
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        // Naver 제공자 이름
        return "naver";
    }

    @Override
    public String getName() {
        // Naver 사용자의 이름
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        // Naver 사용자의 이메일
        return (String) attributes.get("email");
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 원본 attributes 반환
        return attributes;
    }
}
