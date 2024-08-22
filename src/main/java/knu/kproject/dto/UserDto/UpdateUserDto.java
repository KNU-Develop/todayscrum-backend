package knu.kproject.dto.UserDto;

import knu.kproject.global.MBTI;
import knu.kproject.global.ToolName;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UpdateUserDto {
    private String name;
    private String contact;
    private boolean marketingEmailOptIn;

    private String location;
    private MBTI mbti;
    private String imageUrl;
    private Map<ToolName, String> tools = new HashMap<>();
    private List<String> stacks = new ArrayList<>();
}
