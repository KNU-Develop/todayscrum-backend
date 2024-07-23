package knu.kproject.dto.token;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;

    public TokenRefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
