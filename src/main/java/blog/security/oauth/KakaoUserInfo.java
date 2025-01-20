package blog.security.oauth;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        // Kakao 응답에서 "kakao_account" 필드 안에 사용자 정보가 포함됨
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        // Kakao 고유 ID
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        // Kakao 제공자 이름
        return "kakao";
    }

    @Override
    public String getName() {
        // Kakao 사용자의 이름
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }	

    @Override
    public String getEmail() {
        // Kakao 사용자의 이메일
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 원본 attributes 반환
        return attributes;
    }
}
