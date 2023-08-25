package nomad.backend.config;

import lombok.RequiredArgsConstructor;
import nomad.backend.jwt.JwtAuthenticationProcessingFilter;
import nomad.backend.jwt.JwtService;
import nomad.backend.member.MemberRepository;
import nomad.backend.oauth.CustomOAuth2UserService;
import nomad.backend.oauth.OAuth2LoginFailureHandler;
import nomad.backend.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
        return jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/", "/index.html", "/token", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/cluster/auth/callback").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/")
                                .successHandler(oAuth2LoginSuccessHandler)
                                .failureHandler(oAuth2LoginFailureHandler)
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                )
                .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        // To Do
        // 1) "/", "/css/**", "/images/**", "js/**", "/favicon.ico").permitAll()) 이 페이지들 등에서 어떻게 할지 확인필요
        // 2) login custom page 프론트와의 연결 확인 필요
        return http.build();
    }
}
