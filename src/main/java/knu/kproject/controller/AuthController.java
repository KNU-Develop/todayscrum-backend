package knu.kproject.controller;

import knu.kproject.config.JwtTokenUtil;
import knu.kproject.dto.token.TokenRefreshResponse;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/oauth/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        String refreshToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            refreshToken = authorizationHeader.substring(7);
        }
        if (!jwtTokenUtil.isTokenExpired(refreshToken)) {
            String userId = jwtTokenUtil.extractUserId(refreshToken);
            String newAccessToken = jwtTokenUtil.createAccessToken(userId);
            Api_Response<TokenRefreshResponse> response = Api_Response.<TokenRefreshResponse>builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .content(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(new TokenRefreshResponse(newAccessToken))
                    .build();
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(ErrorCode.TOKEN_MISSING_ERROR.getStatus())
                    .body("Invalid refresh token");
        }
    }
}
