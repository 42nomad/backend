package nomad.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nomad.backend.member.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class JwtService {

    private final MemberRepository memberRepository;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String authorization;

    private static final String ACCESS_TOKEN_SUB = "AccessToken";
    private static final String REFRESH_TOKEN_SUB = "RefreshToken";
    private static final String CLAIM = "member_id";
    private static final String BEARER = "Bearer ";

    public String createAccessToken(Long memberId) {
        System.out.println("jwtService - access token 발급");
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUB)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(CLAIM, memberId)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken() {
        System.out.println("jwtService - refresh token 발급");
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUB)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(authorization))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        System.out.println("jwtService - extractRefresh");
        Cookie[] cs = request.getCookies();
        if (cs != null) {
            for (Cookie cookie: cs) {
                if ("refresh".equalsIgnoreCase(cookie.getName()))
                    return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public Optional<Long> extractMemberId(String accessToken) {
        try {
            System.out.println("jwtService - extract memberId");
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(CLAIM)
                    .asLong());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        System.out.println("jwtService - set AccessToken header");
//        System.out.println("access - " + accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(authorization, accessToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        System.out.println("jwtService");
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정
        cookie.setPath("http://localhost:3000/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
        cookie.setMaxAge(60 * 60 * 24 * 14); //쿠키 만료시간 설정 2주

        String cookieHeader = cookie.getName() + "=" + cookie.getValue()
                + "; Secure"
                + "; SameSite=None"
                + "; HttpOnly"
                + "; Path=" + cookie.getPath()
                + "; Max-Age=" + cookie.getMaxAge();
//        response.addCookie(cookie);

        response.addHeader("Set-Cookie", cookieHeader);
    }

    public void updateRefreshToken(Long memberId, String refreshToken) {
        System.out.println("jwtService - updateRefreshToken");
//        System.out.println(memberId);
//        System.out.println("refreshToken - " + refreshToken);
        memberRepository.findByMemberId(memberId) // 여기도 Flush 처리를 해줘야 하는 거 아닌지? 안해줘도 되면 filter에서도 이 로직 하나만 쓰는 게 나을 듯
                .ifPresentOrElse(
                        member -> {
                            member.updateRefreshToken(refreshToken);
                            memberRepository.saveAndFlush(member);
                        },
                        () -> new Exception("일치하는 회원이 없습니다.") // 해당 멤버가 없을 때 exception이 터지면 어디로 가는지? 얘만 catch를 따로 해줘야 하나? 이미 앞단에서 member가 존재할 떄를 보고 있는데 exception 이 필요한지
                );
    }

    public boolean isTokenValid(String token) {
        System.out.println("jwtService - tokenValid 검증");
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
