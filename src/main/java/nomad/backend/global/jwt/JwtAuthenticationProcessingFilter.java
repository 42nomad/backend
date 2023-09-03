package nomad.backend.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import nomad.backend.member.MemberRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/token";
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!checkAccessTokenAndAuthentication(request, response, filterChain))
            checkRefreshTokenAndReIssue(request, response);
        filterChain.doFilter(request, response);
    }

    private Optional<Boolean> findMemberAndSaveAuthentication(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findByMemberId(memberId);
        memberOptional.ifPresent(this::saveAuthentication);
        return memberOptional.isPresent() ? Optional.of(true) : Optional.of(false);
    }

    public boolean checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain) throws ServletException, IOException {
        Optional<Boolean> isAuthenticated = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .map(accessToken -> jwtService.extractMemberId(accessToken))
                .flatMap(memberId -> memberId.flatMap(this::findMemberAndSaveAuthentication));
        if (isAuthenticated.orElse(false))
            return true;
        return false;
    }

    public void reIssueRefreshToken(HttpServletResponse response, Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberRepository.saveAndFlush(member);
        jwtService.setRefreshTokenCookie(response, reIssuedRefreshToken);
    }

    public void checkRefreshTokenAndReIssue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        jwtService.extractRefreshToken(request)
            .filter(jwtService::isTokenValid)
            .ifPresent(refreshToken -> memberRepository.findByRefreshToken(refreshToken)
                    .ifPresent(member -> {
                        jwtService.setAccessTokenHeader(response, jwtService.createAccessToken(member.getMemberId()));
                        reIssueRefreshToken(response, member);
                        saveAuthentication(member);
                    }));
    }

    public void saveAuthentication(Member member) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(member.getMemberId(), "",
                        Arrays.asList(new SimpleGrantedAuthority(member.getRole())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
