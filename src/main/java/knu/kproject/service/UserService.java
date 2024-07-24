package knu.kproject.service;

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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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








    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }
}
