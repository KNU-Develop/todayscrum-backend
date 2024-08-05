package knu.kproject.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.kproject.dto.workspace.PutworkspaceDto;
import knu.kproject.entity.User;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import knu.kproject.service.UserService;
import knu.kproject.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


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
        String redirectUrl = String.format("http://localhost:3000/auth/token?accessToken=%s&refreshToken=%s",
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
