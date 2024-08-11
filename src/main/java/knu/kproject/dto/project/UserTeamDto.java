package knu.kproject.dto.project;

import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.ToolInfoDto;
import knu.kproject.entity.user.User;
import knu.kproject.global.MBTI;
import knu.kproject.global.ROLE;
import knu.kproject.global.UserStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserTeamDto {
    private Long id;
    private String name;
    @Email
    private String email;
    private UserStatus status;
    private String socialId;
    private String contact;
    private String location;
    private MBTI mbti;
    private String imageUrl;
    private ROLE role;
    private String color;
    private List<ToolInfoDto> tools = new ArrayList<>();
    private List<String> stackNames;
}


