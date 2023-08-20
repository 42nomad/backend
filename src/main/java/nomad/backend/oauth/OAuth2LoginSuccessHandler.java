package nomad.backend.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nomad.backend.jwt.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
//    private final UserRepository userRepository;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        System.out.println("oauth login 성공.");
        System.out.println(authentication);
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal(); // 이부분 principal도 확인해야 함
        System.out.println(oAuth2User.getMember_id());
//        String accessToken = jwtService.createAccessToken(oAuth2User.getMember_id());
        String accessToken = jwtService.createAccessToken(Long.valueOf(1));
        String refreshToken = jwtService.createRefreshToken();
        jwtService.setAccessTokenHeader(response, accessToken);
        jwtService.setRefreshTokenCookie(response, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getMember_id(), refreshToken);
//        String targetUrl = UriComponentsBuilder.fromUriString("/token")
//                .queryParam("token", accessToken)
//                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, "/token");
    }

}