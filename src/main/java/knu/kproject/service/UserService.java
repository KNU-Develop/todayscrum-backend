package knu.kproject.service;

import knu.kproject.dto.UserDto.ToolDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.Tool;
import knu.kproject.entity.User;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto Convert2UserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setMarketingEmailOptIn(user.isMarketingEmailOptIn());
        dto.setContact(user.getContact());
        dto.setLocation(user.getLocation());
        dto.setMbti(user.getMbti());
        dto.setImageUrl(user.getImageUrl());
        dto.setTools(user.getTools().stream().map(this::Convert2ToolDto).collect(Collectors.toList()));
        return dto;
    }

    private ToolDto Convert2ToolDto(Tool tool) {
        ToolDto dto = new ToolDto();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setEmail(tool.getEmail());
        return dto;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
    }

    @Transactional
    public User updateMyInfo(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(ErrorCode.NOT_FOUND_ERROR));

        user.updateUserInfo(userDto);
        user.getTools().clear();
        userDto.getTools().forEach(toolDto -> {
            Tool tool = new Tool(toolDto.getName(), toolDto.getEmail(), user);
            user.getTools().add(tool);
        });
        userRepository.save(user);
        return user;
    }

    public Optional<User> findBySocialId(String email) {
        return userRepository.findBySocialId(email);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
