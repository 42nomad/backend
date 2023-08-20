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

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUB = "AccessToken";
    private static final String REFRESH_TOKEN_SUB = "RefreshToken";
    private static final String CLAIM = "member_id";
    private static final String BEARER = "Bearer ";

    // access token 생성
    public String createAccessToken(Long memberId) {
        System.out.println("여기는 어세스토큰 발급");
        Date now = new Date();
        return JWT.create() // JWT 토큰을 생성하는 빌더 반환
                .withSubject(ACCESS_TOKEN_SUB) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
                .withClaim(CLAIM, memberId)
                .sign(Algorithm.HMAC512(secretKey)); // HMAC512 알고리즘 사용, application-jwt.yml에서 지정한 secret 키로 암호화
    }

    // refreshToken 생성
    public String createRefreshToken() {
        System.out.println("여기는 리프레시토큰 발급");
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUB)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // 헤더에서 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(authorization))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cs = request.getCookies();
        if (cs != null) {
            for (Cookie cookie: cs) {
                if ("refresh".equalsIgnoreCase(cookie.getName()))
                    return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    // 토큰에서 멤버아이디 꺼내기
    public Optional<Long> extractMemberId(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build() // 반환된 빌더로 JWT verifier 생성
                    .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(CLAIM)
                    .asLong());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        System.out.println("여기는 어세스 토큰 헤더");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(authorization, accessToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        System.out.println("여기는 리프레시 쿠키");
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정
        cookie.setPath("/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
        cookie.setMaxAge(60 * 60 * 24 * 14); //쿠키 만료시간 설정 2주
        response.addCookie(cookie);
    }

    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(Long memberId, String refreshToken) {
        System.out.println("리프레시 토큰 업데이트");
        memberRepository.findById(memberId)
                .ifPresentOrElse(
                        member -> member.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
