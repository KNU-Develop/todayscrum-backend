package knu.kproject.dto.UserDto;

import knu.kproject.global.MBTI;
import jakarta.validation.constraints.Email;
import knu.kproject.global.ToolName;
import knu.kproject.global.UserStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AdditionalUserInfo {
    private Long id;
    private String name;
    private UserStatus status;
    private String contact;
    @Email
    private String email;
    private boolean requiredTermsAgree;
    private boolean marketingEmailOptIn;

    private String location;
    private MBTI mbti;
    private String imageUrl;
    private Map<ToolName, String> tools = new HashMap<>();
    private List<String> stacks = new ArrayList<>();
}
