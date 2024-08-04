package knu.kproject.service;

import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.project.InviteDto;
import knu.kproject.dto.project.ProjectDto;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.User;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.ProjectRepositroy;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepositroy projectRepositroy;
    private final ProjectUserRepository projectUserRepository;

    public void createProject(PutProjectDto projectDto, Long workSpaceId) {
        Workspace workspace = workspaceRepository.findById(workSpaceId).orElseThrow(() -> new RuntimeException("not found workspace"));

        Project project = Project.builder()
                .title(projectDto.getTitle())
                .overview(projectDto.getOverview())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .workspace(workspace)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        projectRepositroy.save(project);
    }
    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId){
        List<Project> projects = projectRepositroy.findByWorkspaceId(workspaceId);
        return projects.stream().map(this::convertToDto).toList();
    }
    public ProjectDto getProjectById(Long id){
        Project project = projectRepositroy.findById(id).orElseThrow(() -> new RuntimeException("project not found"));

        return convertToDto(project);
    }
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .map(projectUser -> userRepository.findById(projectUser.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .toList();
        List<UserDto> userDtos = users.stream()
                .map(UserDto::fromEntity)
                .toList();

        return ProjectDto.fromEntity(project, userDtos);

    }
    public void updateProject(Long projectId, PutProjectDto updatedProjectData) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));

        project.setTitle(updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate());

        projectRepositroy.save(project);
    }
    public void deleteProject(Long projectId) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project not found"));
        projectRepositroy.delete(project);
    }
    public List<ProjectUser> findByAllProjectUsers(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }
    public void addUser(InviteDto inviteDto) {
        Project project = projectRepositroy.findById(inviteDto.getProjectId()).orElseThrow(() -> new RuntimeException("not found project"));
        List<String> userNames = inviteDto.getUserNames();
        for (String name : userNames) {
            if (userRepository.existsByName(name)) {
                Long userId = userRepository.findByName(name).getId();
                Long projectId = inviteDto.getProjectId();
                if (!projectUserRepository.existsByProjectIdAndUserId(projectId, userId)) {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setProjectId(projectId);
                    projectUser.setUserId(userId);
                        projectUserRepository.save(projectUser);
                }
            }
        }
    }
정
    public void deleteProjectUser(Long projectId, String userName) {
        User user = userRepository.findByName(userName);

        if (user == null) {
            throw new RuntimeException("not found user");
        }

        List<ProjectUser> projects = projectUserRepository.findByProjectId(projectId);
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getUserId() == user.getId()) {
                projectUserRepository.delete(projects.get(i));
            }
        }
    }
}
