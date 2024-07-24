package knu.kproject.service;

import knu.kproject.config.CustomUserDetails;
import knu.kproject.dto.UserDto.AdminUserDto;
import knu.kproject.dto.UserDto.ToolDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.Tool;
import knu.kproject.entity.User;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setMarketingEmailOptIn(user.isMarketingEmailOptIn());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setMbti(user.getMbti());
        dto.setProfileImage(user.getProfileImage());
        dto.setTools(user.getTools().stream().map(this::toToolDto).collect(Collectors.toList()));
        return dto;
    }

    public AdminUserDto toAdminUserDto(User user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setRequiredTermsAgree(user.isRequiredTermsAgree());
        dto.setMarketingEmailOptIn(user.isMarketingEmailOptIn());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setMbti(user.getMbti());
        dto.setStatus(user.getStatus());
        dto.setOauth2Id(user.getOauth2Id());
        dto.setRole(user.getRole());
        dto.setProfileImage(user.getProfileImage());
        dto.setTools(user.getTools().stream().map(this::toToolDto).collect(Collectors.toList()));
        return dto;
    }

    private ToolDto toToolDto(Tool tool) {
        ToolDto dto = new ToolDto();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setNameId(tool.getNameId());
        return dto;
    }

    //내 정보 조회
    @Transactional(readOnly = true)
    public User getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
//      추가정보저장
//    @Transactional
//    public User updateAdditionalInfo(Long userId, UserDto userDto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
//
//        user.setMbti(userDto.getMbti());
//        user.setTools(userDto.getTools().stream()
//                .map(toolDto -> {
//                    Tool tool = new Tool();
//                    tool.setName(toolDto.getName());
//                    tool.setNameId(toolDto.getNameId());
//                    tool.setUser(user);
//                    return tool;
//                })
//                .collect(Collectors.toList()));
//        user.setNeedsAdditionalInfo(false); // 추가 정보 입력 완료
//
//        userRepository.save(user);
//        return user;
//    }

    //내 정보 수정
    @Transactional
    public User updateMyInfo(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setMbti(userDto.getMbti());
        user.setProfileImage(userDto.getProfileImage());
        List<Tool> tools = userDto.getTools().stream()
                .map(toolDto -> {
                    Tool tool = new Tool();
                    tool.setId(toolDto.getId());
                    tool.setName(toolDto.getName());
                    tool.setNameId(toolDto.getNameId());
                    tool.setUser(user);
                    return tool;
                })
                .collect(Collectors.toList());
        user.setTools(tools);

        userRepository.save(user);
        return user;
    }

    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    //user 정보 수정
    @Transactional
    public User updateUserInfo(AdminUserDto adminUserDto) {
        User user = userRepository.findById(adminUserDto.getId())
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
        user.setName(adminUserDto.getName());
        user.setRequiredTermsAgree(adminUserDto.isRequiredTermsAgree());
        user.setMarketingEmailOptIn(adminUserDto.isMarketingEmailOptIn());
        user.setPhone(adminUserDto.getPhone());
        user.setAddress(adminUserDto.getAddress());
        user.setMbti(adminUserDto.getMbti());
        user.setStatus(adminUserDto.getStatus());
        user.setOauth2Id(adminUserDto.getOauth2Id());
        user.setRole(adminUserDto.getRole());
        user.setProfileImage(adminUserDto.getProfileImage());
        List<Tool> tools = adminUserDto.getTools().stream()
                .map(toolDto -> {
                    Tool tool = new Tool();
                    tool.setId(toolDto.getId());
                    tool.setName(toolDto.getName());
                    tool.setNameId(toolDto.getNameId());
                    tool.setUser(user);
                    return tool;
                })
                .collect(Collectors.toList());
        user.setTools(tools);
        userRepository.save(user);
        return user;
    }

    //user 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
