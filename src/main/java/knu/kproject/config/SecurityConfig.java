package knu.kproject.config;


import knu.kproject.config.jwt.JwtAuthenticationFilter;
import knu.kproject.config.jwt.JwtTokenUtil;
import knu.kproject.config.oauth2.CustomAuthenticationEntryPoint;
import knu.kproject.config.oauth2.CustomOAuth2AuthorizationRequestResolver;
import knu.kproject.config.oauth2.CustomOAuth2LoginSuccessHandler;
import knu.kproject.config.oauth2.CustomOAuth2UserService;
import knu.kproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;  // 추가


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/workspace/**").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
                        )
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(customOAuth2LoginSuccessHandler)
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint)  // 인증되지 않은 사용자를 위한 핸들러 등록
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
