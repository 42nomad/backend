package nomad.backend.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.jwt.JwtService;
import nomad.backend.global.oauth.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal(); // 이 부분 리팩토링 가능하면 리팩토링
        String accessToken = jwtService.createAccessToken(oAuth2User.getMember_id());
        String refreshToken = jwtService.createRefreshToken();
        jwtService.setRefreshTokenCookie(response, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getMember_id(), refreshToken);
        String targetUrl = UriComponentsBuilder.fromUriString("https://42nomad.kr/token")
                .queryParam("token", accessToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}