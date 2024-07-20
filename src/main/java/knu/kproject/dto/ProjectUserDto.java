package knu.kproject.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectUserDto {
    private Long projectId;
    private UUID userId;
}
