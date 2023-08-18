package nomad.backend.oauth;

import java.util.Map;

public class Seoul42OAuth2UserInfo extends OAuth2UserInfo {
    public Seoul42OAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getLogin() {
        return (String) attributes.get("login");
    }

    @Override
    public String getImgUrl() {
        Map<String, Object> image = (Map<String, Object>) attributes.get("image");

//        if (image == null) {
//            return null;
//        }

        return (String) image.get("link");
    }
}
