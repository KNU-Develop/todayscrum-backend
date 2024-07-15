package knu.kproject.service;

import knu.kproject.dto.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.ProjectRepositroy;
import knu.kproject.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepositroy projectRepositroy;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    public Project createProject(ProjectDto projectDto) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(projectDto.getWorkspaceId());
        if (optionalWorkspace.isEmpty()) {
            throw new RuntimeException("Workspace not found");
        }

        Workspace workspace = optionalWorkspace.get();

        Project project = Project.builder()
                .title(projectDto.getTitle())
                .overview(projectDto.getOverview())
                .startDate(projectDto.getStarDate())
                .endDate(projectDto.getEndDate())
                .workspace(workspace)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return projectRepositroy.save(project);
    }
    public Optional<Project> getProjectById(Long id){
        return projectRepositroy.findById(id);
    }
    public List<Project> getProjectByWorkspaceId(Long workspaceId){
        return projectRepositroy.findByWorkspaceId(workspaceId);
    }
    public Project updateProject(Long projectId, Project updatedProjectData) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));
        project.setTitle(updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate());

        return projectRepositroy.save(project);
    }
    public void deleteProject(Long projectId) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));
        projectRepositroy.delete(project);
    }
}
