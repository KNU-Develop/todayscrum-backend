package knu.kproject.dto.UserDto;


import lombok.Data;

import java.util.List;

@Data
public class TeamUserInfo {
    private String name;
    private String socialId;
    private String phone;
    private String address;
    private String mbti;
    private List<ToolDto> tools;
    private String imagePath;
}
