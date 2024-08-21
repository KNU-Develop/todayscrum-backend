package knu.kproject.service;

import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.JoinUserDto;
import knu.kproject.dto.UserDto.UpdateUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.user.*;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.ToolName;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
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
    private final StackRepository stackRepository;
    private final UserStackRepository userStackRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    @Transactional
    public void updateUserInfo(Long userId, UpdateUserDto updateUserDto) {
        try {
            User user = findById(userId);
            user.updateUserInfo(updateUserDto);
            saveUserTools(user, updateUserDto.getTools());
            saveUserStacks(user, updateUserDto.getStacks());
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.UPDATE_ERROR);
        }
    }

    @Transactional
    public void addUserInfo(Long userId, AdditionalUserInfo additionalUserInfo) {
        try {
            User user = findById(userId);
            user.addAdditionalInfo(additionalUserInfo);
            saveUserTools(user, additionalUserInfo.getTools());
            saveUserStacks(user, additionalUserInfo.getStacks());
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.INSERT_ERROR);
        }
    }

    @Transactional
    public void joinUser(Long userId, JoinUserDto joinUserDto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
            user.joinInfo(joinUserDto);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.INSERT_ERROR);
        }
    }

    public UserDto getUserInfo(Long userId) {
        try {
            User user = findById(userId);
            return UserDto.fromEntity(user);
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.SELECT_ERROR);
        }
    }

    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }

    @Transactional
    public void withdraw(Long userId, UserDto userDto) {
        try {
            User user = findById(userId);
            user.withDraw(userDto);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.UPDATE_ERROR);
        }
    }

    private void saveUserTools(User user, Map<ToolName, String> tools) {
        try {
            for (Map.Entry<ToolName, String> entry : tools.entrySet()) {
                ToolName toolName = entry.getKey();
                String toolEmail = entry.getValue();

                UserTool userTool = userToolRepository.findByUserAndTool(user, toolName)
                        .orElse(new UserTool(user, toolName, toolEmail));
                userTool.setEmail(toolEmail);
                userToolRepository.save(userTool);
            }
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.UPDATE_ERROR);
        }
    }

    private void saveUserStacks(User user, List<String> stacks) {
        try {
            userStackRepository.deleteByUser(user);
            for (String stackName : stacks) {
                Stack stack = stackRepository.findByName(stackName)
                        .orElseGet(() -> stackRepository.save(new Stack(stackName)));

                if (userStackRepository.findByUserAndStack(user, stack).isEmpty()) {
                    UserStack userStack = new UserStack(user, stack);
                    userStackRepository.save(userStack);
                }
            }
        } catch (Exception e) {
            throw new UserExceptionHandler(ErrorCode.UPDATE_ERROR);
        }
    }
}
