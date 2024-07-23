package knu.kproject.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProjectDto {
    private String title;
    private String overview;
    private Timestamp starDate;
    private Timestamp endDate;
    private Long workspaceId;
}
