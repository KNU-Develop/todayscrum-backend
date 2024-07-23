package knu.kproject.controller;

import knu.kproject.config.JwtTokenUtil;
import knu.kproject.dto.token.TokenRefreshRequest;
import knu.kproject.dto.token.TokenRefreshResponse;
import knu.kproject.entity.User;
import knu.kproject.global.code.ApiResponse;
import knu.kproject.global.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/oauth/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenUtil.isTokenExpired(refreshToken)) {
            String socialId = jwtTokenUtil.extractSocialId(refreshToken);
            String newAccessToken = jwtTokenUtil.createAccessToken(socialId);
            ApiResponse<TokenRefreshResponse> response = ApiResponse.<TokenRefreshResponse>builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .msg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(new TokenRefreshResponse(newAccessToken))
                    .build();
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }
}
