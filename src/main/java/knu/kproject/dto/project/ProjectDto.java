package knu.kproject.dto.project;

import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private String title;
    private String overview;
    private Timestamp startDate;
    private Timestamp endDate;
    private Long workspaceId;
    private List<User> users;

    public ProjectDto(Project project, List<User> users) {
        this.title = project.getTitle();
        this.overview = project.getOverview();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.users = users;
    }
}
