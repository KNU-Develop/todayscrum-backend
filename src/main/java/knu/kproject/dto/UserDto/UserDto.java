package knu.kproject.dto.UserDto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String socialId;
    private boolean marketingEmailOptIn;
    private String phone;
    private String address;
    private String mbti;
    private List<ToolDto> tools;

}
