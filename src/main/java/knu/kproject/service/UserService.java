package knu.kproject.service;

import knu.kproject.entity.User;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }
}
