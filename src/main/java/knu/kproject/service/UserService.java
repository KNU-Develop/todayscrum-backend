package knu.kproject.service;

import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.JoinUserDto;
import knu.kproject.dto.UserDto.UpdateUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.user.*;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.ToolName;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
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
                .orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
    }

    @Transactional
    public void updateUserInfo(Long userId, UpdateUserDto updateUserDto) {
        User user = findById(userId);
        try {
            user.updateUserInfo(updateUserDto);
            saveUserTools(user, updateUserDto.getTools());
            saveUserStacks(user, updateUserDto.getStacks());
        } catch (DataIntegrityViolationException e) {
            throw new UserExceptionHandler(UserErrorCode.INVALID_USER_DATA);
        } catch (Exception e) {
            throw new UserExceptionHandler(UserErrorCode.UPDATE_ERROR);
        }
    }

    @Transactional
    public void addUserInfo(Long userId, AdditionalUserInfo additionalUserInfo) {
        User user = findById(userId);
        try {
            user.addAdditionalInfo(additionalUserInfo);
            saveUserTools(user, additionalUserInfo.getTools());
            saveUserStacks(user, additionalUserInfo.getStacks());
        } catch (DataIntegrityViolationException e) {
            // 데이터 무결성 위반 시 처리
            throw new UserExceptionHandler(UserErrorCode.INVALID_USER_DATA);
        } catch (Exception e) {
            // 기타 예기치 않은 모든 오류 처리
            throw new UserExceptionHandler(UserErrorCode.INSERT_ERROR);
        }
    }


    public void joinInfo(Long userId, JoinUserDto joinUserDto) {
        User user = findById(userId);
        try {
            if (!joinUserDto.isRequiredTermsAgree()) {
                throw new UserExceptionHandler(UserErrorCode.INVALID_USER_DATA, "User must agree to the required terms.");
            }
            user.joinInfo(joinUserDto);
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserExceptionHandler(UserErrorCode.INVALID_USER_DATA);
        } catch (Exception e) {
            throw new UserExceptionHandler(UserErrorCode.INSERT_ERROR);
        }

    }


    public UserDto getUserInfo(Long userId) {
        try {
            User user = findById(userId);
            return UserDto.fromEntity(user);
        } catch (Exception e) {
            throw new UserExceptionHandler(UserErrorCode.SELECT_ERROR);
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
            throw new UserExceptionHandler(UserErrorCode.UPDATE_ERROR);
        }
    }

    private void saveUserTools(User user, Map<ToolName, String> tools) {
        try {
            for (Map.Entry<ToolName, String> entry : tools.entrySet()) {
                ToolName toolName = entry.getKey();
                String toolEmail = entry.getValue();
                if (!EnumSet.allOf(ToolName.class).contains(toolName)) {
                    throw new UserExceptionHandler(UserErrorCode.INVALID_USER_DATA, "Invalid tool name provided: GITHUB or NOTION or FIGMA");
                }
                UserTool userTool = userToolRepository.findByUserAndTool(user, toolName)
                        .orElse(new UserTool(user, toolName, toolEmail));
                userTool.setEmail(toolEmail);
                userToolRepository.save(userTool);
            }
        } catch (Exception e) {
            throw new UserExceptionHandler(UserErrorCode.UPDATE_ERROR);
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
            throw new UserExceptionHandler(UserErrorCode.UPDATE_ERROR);
        }
    }
}
