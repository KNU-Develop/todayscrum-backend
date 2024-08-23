package knu.kproject.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.ToolInfoDto;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.MBTI;
import knu.kproject.global.ROLE;
import knu.kproject.global.UserStatus;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserTeamDto {
    private Long id;
    private String name;
    @Email
    private String email;
    private String location;
    private MBTI mbti;
    private String imageUrl;
    private ROLE role;
    private String color;
    private List<ToolInfoDto> tools = new ArrayList<>();
    private List<String> stackNames;

    public static UserTeamDto fromEntity(ProjectUser projectUser) {
        return UserTeamDto.builder()
                .id(projectUser.getUser().getId())
                .name(projectUser.getUser().getName())
                .email(projectUser.getUser().getEmail())
                .location(projectUser.getUser().getLocation())
                .mbti(projectUser.getUser().getMbti())
                .imageUrl(projectUser.getUser().getImageUrl())
                .role(projectUser.getRole())
                .color(projectUser.getColor())
                .tools(projectUser.getUser().getUserTools().stream()
                        .map(ToolInfoDto::fromEntity)
                        .toList())
                .stackNames(projectUser.getUser().getUserStacks().stream()
                        .map(userStack -> userStack.getStack().getName())
                        .toList())
                .build();
    }
}


