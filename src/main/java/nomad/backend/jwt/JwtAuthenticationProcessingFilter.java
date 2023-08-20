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
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/Login"; // "/login"으로 들어오는 요청은 Filter 작동 X front랑 상의

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        // access 검증 유효하면 통과, 유효하지 않으면 refresh 검증 refresh 있으면 access 재발급 없으면 expired 오류 보내서 login으로 가게끔
        // access 검증 유효하면 걍 통과 다음 필터 진행 (auth 받아진 상태겠지?)
        // access 유효하지 않으면 refresh 검증 유효하면 access 재발급 auth
        // refresh 유효하지 않으면 아무것도 안하고 필터 흘러가서 oauth로 가게끔

        checkAccessTokenAndAuthentication(request, response, filterChain);
        checkRefreshTokenAndAuthentication(request, response, filterChain);
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain) throws ServletException, IOException {
        // accesstoken이 유효하면 인증 저장하고 필터체인 넘겨서 oauth 진행안하는 것이 나의 그림
        System.out.println("여긴 들어오나요?");
        System.out.println("request = " + jwtService.extractAccessToken(request));
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractMemberId(accessToken)
                        .ifPresent(memberId -> memberRepository.findById(memberId)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    public void reIssueRefreshToken(HttpServletResponse response, Member member) {
        System.out.println("여긴 들어오면 안될텐데..?");
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
//        userRepository.saveAndFlush(user); // transacitonal 느낌인데 잘 모르겠음
        jwtService.setRefreshTokenCookie(response, reIssuedRefreshToken);
    }

    public void checkRefreshTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {

            jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(refreshToken -> memberRepository.findByRefreshToken(refreshToken)
                        .ifPresent(member -> {
                            jwtService.setAccessTokenHeader(response, jwtService.createAccessToken(member.getMember_id()));
                            reIssueRefreshToken(response, member);
                            saveAuthentication(member);
                        }));
    }

    public void saveAuthentication(Member member) {
        System.out.println("여기도 들어오나요???????");
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(member, "",
//                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(member.getIntra())
                .password("")
                .roles("USER")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        System.out.println("들어오긴 하나요");
        String[] excludePath = {"/token", "/"};
        String path = request.getRequestURI();
        System.out.println(Arrays.stream(excludePath).anyMatch(path::startsWith));
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }
}
