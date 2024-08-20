package knu.kproject.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import knu.kproject.dto.UserDto.ToolInfoDto;
import knu.kproject.entity.user.User;
import knu.kproject.global.MBTI;
import knu.kproject.global.ROLE;
import knu.kproject.global.UserStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
}


