package knu.kproject.service;

import knu.kproject.dto.project.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.User;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.ProjectRepositroy;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepositroy projectRepositroy;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ProjectUserRepository projectUserRepository;

    public Project createProject(ProjectDto projectDto, Long workSpaceId) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(workSpaceId);
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

        return projectRepositroy.save(project);
    }
    public Optional<Project> getProjectById(Long id){
        return projectRepositroy.findById(id);
    }
    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId){
//        return projectRepositroy.findByWorkspaceId(workspaceId);
        List<Project> projects = projectRepositroy.findByWorkspaceId(workspaceId);
        return projects.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .map(projectUser -> userRepository.findById(projectUser.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
        return new ProjectDto(project, users);
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
    public List<ProjectUser> findByAllProjectUsers(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }
    public String addUser(Long projectId, Long userId) {
        if (projectRepositroy.existsById(projectId) && userRepository.existsById(userId)) {
            if (projectUserRepository.existsByUserId(userId)) {
                return "exist user";
            }
            ProjectUser projectUser = new ProjectUser();
            projectUser.setProjectId(projectId);
            projectUser.setUserId(userId);

            projectUserRepository.save(projectUser);
            return "success";
        }
        return "fail";
    }

    public void deleteProjectUser(Long userId) {
        ProjectUser projectUser = projectUserRepository.findByUserId(userId);
        projectUserRepository.delete(projectUser);
    }
}
