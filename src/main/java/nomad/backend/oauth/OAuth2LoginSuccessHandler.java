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

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        System.out.println("successHandler - oauth login");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal(); // 이부분 principal도 확인해야 함 실제 멤버 생성했을 때 어떻게 오는지 확인 필요
        System.out.println(oAuth2User.getMember_id());
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