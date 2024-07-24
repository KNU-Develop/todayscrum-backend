package knu.kproject.dto.UserDto;


import lombok.Data;

import java.util.List;

@Data
public class AdminUserDto {
    private Long id;
    private String name;
    private String socialId;
    private boolean requiredTermsAgree;
    private boolean marketingEmailOptIn;
    private String phone;
    private String address;
    private String mbti;
    private String status;
    private String oauth2Id;
    private String role;
    private List<ToolDto> tools;
}
