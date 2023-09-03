package nomad.backend.global.oauth;

import lombok.Builder;
import lombok.Getter;
import nomad.backend.member.Member;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new Seoul42OAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity(String intra) {
        return Member.builder()
                .intra(intra)
                .home(0)
                .role("ROLE_USER")
                .build();
    }
}