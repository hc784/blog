package blog.security.oauth;

import java.util.Map;

public interface OAuth2UserInfo {
    /**
     * 제공자의 고유 ID를 반환
     */
    String getProviderId();

    /**
     * OAuth2 제공자 이름 반환 (e.g., google, naver, kakao)
     */
    String getProvider();

    /**
     * 사용자의 이름 반환
     */
    String getName();

    /**
     * 사용자의 이메일 반환
     */
    String getEmail();

    /**
     * 원본 attributes 반환 (선택적)
     */
    Map<String, Object> getAttributes();
}
