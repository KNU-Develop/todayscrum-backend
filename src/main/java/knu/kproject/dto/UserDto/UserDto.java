package knu.kproject.dto.UserDto;

import knu.kproject.entity.MBTI;
import knu.kproject.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String status;
    private String socialId;
    private boolean marketingEmailOptIn;
    private String contact;
    private String location;
    private MBTI mbti;
    private String imageUrl;
    private List<ToolDto> tools = new ArrayList<>();
    private List<String> stackNames;


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

        dto.setTools(user.getUserTools().stream()
                .map(ToolDto::fromEntity)
                .collect(Collectors.toList()));

        dto.setStackNames(user.getUserStacks().stream()
                .map(userStack -> userStack.getStack().getName())
                .collect(Collectors.toList()));

        return dto;
    }
}

