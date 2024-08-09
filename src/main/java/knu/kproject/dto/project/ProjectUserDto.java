package knu.kproject.dto.project;

import knu.kproject.entity.ROLE;
import lombok.Data;

@Data
public class ProjectUserDto {
    private Long projectId;
    private String userId;
    private ROLE role;
    private String color;
}
