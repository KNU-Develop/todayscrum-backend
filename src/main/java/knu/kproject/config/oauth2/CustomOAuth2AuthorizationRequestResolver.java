package knu.kproject.config.oauth2;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;


@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        registerFinalRedirectURI(authorizationRequest, request);
        return authorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        registerFinalRedirectURI(authorizationRequest, request);
        return authorizationRequest;
    }

    private void registerFinalRedirectURI(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest != null) {
//            String requestURL = request.getRequestURL().toString();
//            if (!requestURL.isEmpty()) {
//                request.getSession().setAttribute("requestURL", requestURL);
//            }
            String origin = getOriginUri(request);
            if (!origin.isEmpty()) {
                request.getSession().setAttribute("origin_url", origin);
            }
        }

    }
    private String getOriginUri(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String redirectUri = "";
        if (request.getParameter("redirect_uri") != null) {
            redirectUri = request.getParameter("redirect_uri");
        } else if (clientIp.contains(":")) {
            redirectUri = "http://[" + clientIp + "]";
        } else {
            redirectUri = "http://" + clientIp;
        }
        return redirectUri;
    }
}
