package knu.kproject.config.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.kproject.config.jwt.JwtTokenUtil;
import knu.kproject.dto.workspace.PutworkspaceDto;
import knu.kproject.entity.user.User;
import knu.kproject.entity.workspace.Workspace;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import knu.kproject.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtil jwtTokenUtil;

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String socialId = oauth2User.getAttribute("socialId");
        String accessToken = jwtTokenUtil.createAccessToken(socialId);
        String refreshToken = jwtTokenUtil.createRefreshToken(socialId);

        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new RuntimeException("user not found"));

        List<Workspace> workspaces = workspaceRepository.findByOwnerId(user.getId());

        if (workspaces.isEmpty()) {
            PutworkspaceDto workSpaceDto = new PutworkspaceDto();
            workSpaceDto.setTitle("My Workspace");
            workSpaceDto.setDescription("기본 워크스페이스 입니다.");

            workspaceService.createWorkSpace(workSpaceDto, user.getId());
        }

//        String requestURL = request.getSession().getAttribute("requestURL").toString();
//        String origin;
//        if (requestURL == null || requestURL.isEmpty())
//            origin = "http://localhost:3000";
//        else {
//            origin = requestURL.split("/oauth2")[0];
//        }
//        request.getSession().removeAttribute("requestURL");

        String origin = request.getSession().getAttribute("origin_url").toString();
        if (origin == null || origin.isEmpty()) {
            origin = "NOT_FOUND_ORIGIN";
        }
        String redirectUrl = String.format("%s/auth/token?accessToken=%s&refreshToken=%s",
                origin,
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
