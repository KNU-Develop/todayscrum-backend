package knu.kproject.dto.UserDto;

import knu.kproject.entity.MBTI;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AdditionalUserInfo {
    private Long id;
    private String name;
    private String status;
    private String contact;
    private String email;
    private boolean requiredTermsAgree;
    private boolean marketingEmailOptIn;

    private String location;
    private MBTI mbti;
    private String imageUrl;
    private Map<String, String> tools = new HashMap<>();
    private List<String> stacks = new ArrayList<>();
}
