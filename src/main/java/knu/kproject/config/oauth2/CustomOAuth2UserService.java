package knu.kproject.config.oauth2;


import knu.kproject.entity.User;
import knu.kproject.entity.UserStatus;
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
        String userEmail = attributes.get("userEmail").toString();
        Optional<User> user = userRepository.findBySocialId(socialId);
        if (user.isEmpty()) {
            User newUser = new User(userName, socialId, userEmail, UserStatus.LOGIN);
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
        String userName = null, userId = null, userEmail = null;
        switch (registrationId) {
            case "github" -> {
                userName = attributes.get("name").toString();
                userId = attributes.get("id").toString();
                userEmail = attributes.get("login").toString()+"@github.com";
            }
            case "google" -> {
                userName = attributes.get("name").toString();
                userId = attributes.get("sub").toString();
                userEmail = attributes.get("email").toString();
            }
            case "kakao" -> {
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                userName = properties.get("nickname").toString();
                userId = attributes.get("id").toString();
                userEmail = kakaoAccount.get("email").toString();
            }
        };
        attributes.put("userName", userName);
        attributes.put("socialId", userId);
        attributes.put("userEmail", userEmail != null ? userEmail : "");
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
