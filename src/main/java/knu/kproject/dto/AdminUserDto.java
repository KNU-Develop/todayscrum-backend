package knu.kproject.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdminUserDto {
    private UUID id;
    private String name;
    private String email;
    private boolean onboardingCompleted;
    private String phone;
    private String address;
    private String mbti;
    private String profilePicture;
    private List<String> stack;
    private ToolDto tool;
}
