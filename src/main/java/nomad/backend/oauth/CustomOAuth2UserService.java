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
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes extractAttributes = OAuthAttributes.of(userNameAttributeName, attributes);

        Member createdUser = getUser(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
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

    private Member saveUser(OAuthAttributes attributes) {
        Member createdUser = attributes.toEntity(attributes.getOauth2UserInfo().getLogin());
//        return userRepository.save(createdUser);
        return new Member(attributes.getOauth2UserInfo().getLogin());
    }
}
