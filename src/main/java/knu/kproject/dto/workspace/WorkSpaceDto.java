package knu.kproject.dto.workspace;

import knu.kproject.dto.project.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSpaceDto {
    private Long id;
    private Long ownerId;
    private String title;
    private String description;
    private Timestamp createdAt;
    private List<ProjectDto> projects;
}
