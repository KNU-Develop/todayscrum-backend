package knu.kproject.dto.project;

import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private UUID id;
    private String title;
    private String overview;
    private Timestamp startDate;
    private Timestamp endDate;
    private Long workspaceId;
    private List<UserDto> users;
    private List<BoardDto> boards = new ArrayList<>();

    public static ProjectDto fromEntity(Project project, List<UserDto> users) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setOverview(project.getOverview());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setWorkspaceId(project.getWorkspace().getId());
        dto.setUsers(users);

        return dto;
    }
}
