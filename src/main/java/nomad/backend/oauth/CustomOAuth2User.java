package nomad.backend.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private Long member_id;
    private String intra; // 나중에 지워도 됨

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            Long member_id, String login) {
        super(authorities, attributes, nameAttributeKey);
        this.member_id = member_id;
        this.intra = login;
    }
}
