package knu.kproject.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.entity.project.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDto {
    private UUID id;
    private String title;
    private String overview;
    private Timestamp startDate;
    private Timestamp endDate;
    private Long workspaceId;
    private String color;
    private List<UserTeamDto> users;

    public static ProjectDto fromEntity(Project project, List<UserTeamDto> users) {
        ProjectDto dto = ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .overview(project.getOverview())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .workspaceId(project.getWorkspace().getId())
                .users(users)
                .color(project.getColor())
                .build();

        return dto;
    }
}
