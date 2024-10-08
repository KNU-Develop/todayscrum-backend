package knu.kproject.config.jwt;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.kproject.config.SecurityConfig;
import knu.kproject.entity.user.User;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
            try {
                userId = (jwtTokenUtil.extractUserId(accessToken));
            } catch (Exception e) {
                response.sendError(ErrorCode.TOKEN_MISSING_ERROR.getStatus(), "Invalid token");
                return;
            }
        }
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtTokenUtil.validateToken(accessToken, userId)) {
                User user = userService.findBySocialId(userId).orElse(null);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, new ArrayList<>());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.sendError(ErrorCode.TOKEN_MISSING_ERROR.getStatus(), "Invalid token");
                    return;
                }
            }
        } else if (userId != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            if (jwtTokenUtil.validateToken(accessToken, userId)) {
                User user = userService.findBySocialId(userId).orElse(null);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.sendError(ErrorCode.TOKEN_MISSING_ERROR.getStatus(), "Invalid token");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
}
