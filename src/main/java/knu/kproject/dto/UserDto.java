package knu.kproject.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDto {
    private String id;
    private String name;
    private String contact;
    private String laction;
    private String mbti;
    private String profilePicture;
    private Timestamp createdAt;
}
