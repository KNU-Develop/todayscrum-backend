package knu.kproject.dto;

import lombok.Data;

import java.util.List;


@Data
public class UserDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String mbti;
    private String profilePicture;
    private List<String> stack;
    private ToolDto tool;
    private boolean onboardingCompleted;
}
