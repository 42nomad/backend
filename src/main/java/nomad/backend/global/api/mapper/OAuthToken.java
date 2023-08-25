package nomad.backend.global.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * 42api 토큰 정보 매핑용 클래스
 * @version 1.0
 * @author hyunjcho
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int created_at;
}
