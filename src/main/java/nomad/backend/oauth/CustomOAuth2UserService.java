package nomad.backend.oauth;

import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService(); // oauth 유저 정보 받아주는 default 클래스
        OAuth2User oAuth2User = service.loadUser(userRequest); // oauth2UserService 구현시 필수 오버라이드 메소드. oauth 유저 정보 추출해서 사용

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes extractAttributes = OAuthAttributes.of(userNameAttributeName, attributes);

        Member createdUser = getUser(extractAttributes); // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 우리는 role이 필요 없을 것 같은데 어떻게 고쳐야 할지;
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getMember_id()
        );
    }

    private Member getUser(OAuthAttributes attributes) {
        Member findUser = null;
//        Member findUser = memberRepository.findByIntra(attributes.getOauth2UserInfo().getLogin()).orElse(null);
        // 인트라 아이디로 멤버 찾고, 있으면 파인드유저 반환 없으면 세이브 유저 반환
        if (findUser == null)
            return saveUser(attributes);
        return findUser;
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private Member saveUser(OAuthAttributes attributes) {
        Member createdUser = attributes.toEntity(attributes.getOauth2UserInfo().getLogin());
//        return userRepository.save(createdUser);
        return new Member(attributes.getOauth2UserInfo().getLogin());
    }
}
