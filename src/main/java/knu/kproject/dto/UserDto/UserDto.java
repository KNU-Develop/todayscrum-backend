package knu.kproject.dto.UserDto;

import jakarta.validation.constraints.Email;
import knu.kproject.global.ROLE;
import knu.kproject.global.MBTI;
import knu.kproject.entity.user.User;
import knu.kproject.global.UserStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
    private UserStatus status;
    private String socialId;
    private boolean marketingEmailOptIn;
    private String contact;
    private String location;
    private MBTI mbti;
    private String imageUrl;
    private ROLE role;
    private List<ToolInfoDto> tools = new ArrayList<>();
    private List<String> stackNames;
    private String color;


    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setStatus(user.getStatus());
        dto.setEmail(user.getEmail());
        dto.setMarketingEmailOptIn(user.isMarketingEmailOptIn());
        dto.setContact(user.getContact());
        dto.setLocation(user.getLocation());
        dto.setMbti(user.getMbti());
        dto.setImageUrl(user.getImageUrl());
        dto.setRole(user.getRole());
        dto.setColor(user.getColor());

        dto.setTools(user.getUserTools().stream()
                .map(ToolInfoDto::fromEntity)
                .collect(Collectors.toList()));

        dto.setStackNames(user.getUserStacks().stream()
                .map(userStack -> userStack.getStack().getName())
                .collect(Collectors.toList()));

        return dto;
    }
}

