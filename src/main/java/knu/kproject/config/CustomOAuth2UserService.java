package knu.kproject.config;


import knu.kproject.entity.User;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        addSocialIdentifierInfo(attributes, registrationId);

        String userName = attributes.get("userName").toString();
        String socialId = attributes.get("socialId").toString();
        Optional<User> user = userRepository.findBySocialId(socialId);
        if (user.isEmpty()) {
            User newUser = new User(userName, socialId, false, false, "LOGIN");
            userRepository.save(newUser);
        }

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), // default role
                        attributes,
                        userNameAttributeName
        );
    }

    private void addSocialIdentifierInfo(Map<String, Object> attributes, String registrationId) {
        String userName = null, userId = null;
        switch (registrationId) {
            case "github" -> {
                userName = attributes.get("login").toString();
                userId = attributes.get("id").toString();
            }
            case "google" -> {
                userName = attributes.get("name").toString();
                userId = attributes.get("sub").toString();
            }
            case "kakao" -> {
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                userName = properties.get("nickname").toString();
                userId = attributes.get("id").toString();
            }
        };
        attributes.put("userName", userName);
        attributes.put("socialId", userId);
    }
    private void printOAuth2UserAttributes(OAuth2User oAuth2User) {
        System.out.println("OAuth2User attributes:");

        // 사용자 기본 속성 출력
        oAuth2User.getAttributes().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        // GrantedAuthorities 출력
        System.out.println("Granted Authorities:");
        oAuth2User.getAuthorities().forEach(authority -> {
            System.out.println(authority.getAuthority());
        });

        // OAuth2User name 출력
        System.out.println("Name: " + oAuth2User.getName());
    }
}
