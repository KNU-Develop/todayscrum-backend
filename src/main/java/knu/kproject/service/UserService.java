package knu.kproject.service;

import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.ToolDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.*;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserToolRepository userToolRepository;
    private final ToolRepository toolRepository;
    private final StackRepository stackRepository;
    private final UserStackRepository userStackRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    @Transactional
    public void updateUserInfo(Long userId, AdditionalUserInfo additionalUserInfo) {
        User user = findById(userId);
        user.updateUserInfo(additionalUserInfo);
        saveUserTools(user, additionalUserInfo.getTools());
        saveUserStacks(user, additionalUserInfo.getStacks());
    }

    @Transactional
    public void addUserInfo(Long userId, AdditionalUserInfo additionalUserInfo) {
        User user = findById(userId);
        user.joinInfo(additionalUserInfo);
        saveUserTools(user, additionalUserInfo.getTools());
        saveUserStacks(user, additionalUserInfo.getStacks());
    }

    @Transactional
    public void joinUser(Long userId, AdditionalUserInfo additionalUserInfo) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
            user.joinInfo(additionalUserInfo);
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error joining user with userId {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    public UserDto getUserInfo(Long userId) {
        User user = findById(userId);
        return UserDto.fromEntity(user);
    }

    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }

    @Transactional
    public void withdraw(Long userId, UserDto userDto) {
        User user = findById(userId);
        user.withDraw(userDto);
        userRepository.save(user);
    }

    private void saveUserTools(User user, Map<String, String> tools) {
        for (Map.Entry<String, String> entry : tools.entrySet()) {
            String toolName = entry.getKey();
            String toolEmail = entry.getValue();

            Tool tool = toolRepository.findByName(toolName)
                    .orElseGet(() -> toolRepository.save(new Tool(toolName)));

            UserTool userTool = userToolRepository.findByUserAndTool(user, tool)
                    .orElse(new UserTool(user, tool, toolEmail));
            userTool.setEmail(toolEmail);
            userToolRepository.save(userTool);
        }
    }

    private void saveUserStacks(User user, List<String> stacks) {
        userStackRepository.deleteByUser(user);
        for (String stackName : stacks) {
            Stack stack = stackRepository.findByName(stackName)
                    .orElseGet(() -> stackRepository.save(new Stack(stackName)));

            if (userStackRepository.findByUserAndStack(user, stack).isEmpty()) {
                UserStack userStack = new UserStack(user, stack);
                userStackRepository.save(userStack);
            }
        }
    }
}
