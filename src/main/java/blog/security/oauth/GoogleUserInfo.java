package blog.security.oauth;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        // Google 고유 ID
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        // Google 제공자 이름
        return "google";
    }

    @Override
    public String getName() {
        // Google 사용자의 이름
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        // Google 사용자의 이메일
        return (String) attributes.get("email");
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 원본 attributes 반환
        return attributes;
    }
}
