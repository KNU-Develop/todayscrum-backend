package knu.kproject.service;

import jakarta.transaction.Transactional;
import knu.kproject.dto.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.ProjectRepository;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ProjectUserRepository projectUserRepository;

    public Project createProject(ProjectDto projectDto) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(projectDto.getWorkspaceId());
        if (optionalWorkspace.isEmpty()) {
            throw new RuntimeException("Workspace not found");
        }

        Workspace workspace = optionalWorkspace.get();

        Project project = Project.builder()
                .title(projectDto.getTitle())
                .overview(projectDto.getOverview())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .workspace(workspace)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return projectRepository.save(project);
    }
    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }
    public List<Project> getProjectByWorkspaceId(Long workspaceId){
        return projectRepository.findByWorkspaceId(workspaceId);
    }
    public Project updateProject(Long projectId, Project updatedProjectData) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));
        project.setTitle(updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate());

        return projectRepository.save(project);
    }
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));
        projectRepository.delete(project);
    }
    public List<ProjectUser> findByAllProjectUsers(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }
    @Transactional
    public ProjectUser addUser(Long project, UUID user) {
        if (!projectRepository.existsById(project)) {
            throw new IllegalArgumentException("project is not defined");
        }
        if (!userRepository.existsById(user)) {
            throw new IllegalArgumentException("user is not defined");
        }
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectId(project);
        projectUser.setUserId(user);

        return projectUserRepository.save(projectUser);
    }

    public void deleteProjectUser(UUID uid) {
        ProjectUser projectUser = projectUserRepository.findByUserId(uid);
        projectUserRepository.delete(projectUser);
    }
}
