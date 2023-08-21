package nomad.backend.jwt;

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
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/token";
    // To Do: 이 페이지 Url에 대해서 token을 받아오는 페이지, 처음 Login하는 메인 페이지 등에서 filter를 진행하지 않을 수 있도록 검토 및 token 페이지를 어떻게 할지 고민

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // access 검증 유효하면 통과, 유효하지 않으면 refresh 검증 refresh 있으면 access 재발급 없으면 oauth 필터 진행
        if (checkAccessTokenAndAuthentication(request, response, filterChain)) {
            System.out.println("Filter - Access Check Passed"); // sout 필요 없어지면 메소드 하나로 합치기
        } else {
            System.out.println("Filter - Access Check Failed");
            checkRefreshTokenAndReIssue(request, response);
        }
        filterChain.doFilter(request, response);
    }

    private Optional<Boolean> findMemberAndSaveAuthentication(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        memberOptional.ifPresent(this::saveAuthentication);
        return memberOptional.isPresent() ? Optional.of(true) : Optional.of(false);
    }

    public boolean checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Filter - Access Check");
        Optional<Boolean> isAuthenticated = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .map(accessToken -> jwtService.extractMemberId(accessToken))
                .flatMap(memberId -> memberId.flatMap(this::findMemberAndSaveAuthentication));
        if (isAuthenticated.orElse(false))
            return true;
        return false;
    }

    public void reIssueRefreshToken(HttpServletResponse response, Member member) {
        System.out.println("Filter - refresh reIssue");
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
//        userRepository.saveAndFlush(user); // transactional을 사용할 수 없으니 repository에서 flush 할 수 있도록 설정하기
        jwtService.setRefreshTokenCookie(response, reIssuedRefreshToken);
    }

    public void checkRefreshTokenAndReIssue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("Filter - check refresh");
        jwtService.extractRefreshToken(request)
            .filter(jwtService::isTokenValid)
            .ifPresent(refreshToken -> memberRepository.findByRefreshToken(refreshToken)
                    .ifPresent(member -> {
//                        jwtService.setAccessTokenHeader(response, jwtService.createAccessToken(member.getMember_id()));
                        jwtService.setAccessTokenHeader(response, jwtService.createAccessToken(Long.valueOf(1)));
                        reIssueRefreshToken(response, member);
                        saveAuthentication(member);
                    }));
    }

    public void saveAuthentication(Member member) {
        System.out.println("Filter - save authentication");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(member.getMember_id(), "",
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
