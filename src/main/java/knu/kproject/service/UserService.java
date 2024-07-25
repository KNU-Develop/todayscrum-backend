package knu.kproject.service;

import knu.kproject.config.CustomUserDetails;
import knu.kproject.dto.UserDto.TeamUserInfo;
import knu.kproject.dto.UserDto.ToolDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.Tool;
import knu.kproject.entity.User;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        dto.setImagePath(user.getImagePath());
        dto.setTools(user.getTools().stream().map(this::toToolDto).collect(Collectors.toList()));
        return dto;
    }

    public TeamUserInfo toAdminUserDto(User user) {
        TeamUserInfo dto = new TeamUserInfo();
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setMbti(user.getMbti());
        dto.setImagePath(user.getImagePath());
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

    //내 정보 수정
    @Transactional
    public User updateMyInfo(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        user.updateUserInfo(userDto);

        List<Tool> tools = userDto.getTools().stream()
                .map(toolDto -> new Tool(toolDto.getId(), toolDto.getName(), toolDto.getNameId(), user))
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

    //user 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
