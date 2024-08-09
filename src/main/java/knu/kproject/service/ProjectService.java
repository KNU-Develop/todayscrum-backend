package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Null;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.project.InviteDto;
import knu.kproject.dto.project.ProjectDto;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.dto.project.RoleDto;
import knu.kproject.entity.*;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Work;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.management.relation.Role;
import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepositroy projectRepositroy;
    private final ProjectUserRepository projectUserRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    public ProjectDto fromEntity(Project project, List<UserDto> users) {
        List<Board> boards = boardRepository.findByProject(project);
        project.setBoards(boards);

        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setOverview(project.getOverview());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setWorkspaceId(project.getWorkspace().getId());
        dto.setUsers(users);
        dto.setBoards(project.getBoards().stream()
                .map(boardService::fromEntity)
                .toList());

        return dto;
    }
    public UUID createProject(PutProjectDto projectDto, Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByOwnerId(userId);

        if (userId==null) throw new IllegalArgumentException("illegal error");
        if (workspaces.isEmpty()) throw new EntityNotFoundException("workspace not found");

        Project project = Project.builder()
                .title(projectDto.getTitle())
                .overview(projectDto.getOverview())
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .workspace(workspaces.get(0))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .color(projectDto.getColor())
                .build();

        projectRepositroy.save(project);

        ProjectUser projectUser = ProjectUser.builder()
                .userId(userId)
                .projectId(project.getId())
                .role(ROLE.OWNER)
                .color(projectDto.getColor())
                .build();

        projectUserRepository.save(projectUser);

        return project.getId();
    }
    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId){
        if (workspaceId==null) throw new IllegalArgumentException("error");

        workspaceRepository.findById(workspaceId).orElseThrow(() -> new EntityNotFoundException("workspace not found"));
        List<Project> projects = projectRepositroy.findByWorkspaceOwnerId(workspaceId);
        List<ProjectUser> subProjects = projectUserRepository.findByUserId(workspaceId);
        for (ProjectUser projectUser : subProjects) {
            Project project = projectRepositroy.findById(projectUser.getProjectId()).orElseThrow();
            if (!projects.contains(project)) {
                projects.add(project);
            }
        }

        return projects.stream().map(this::convertToDto).toList();
    }
    public ProjectDto getProjectById(Long userToken, UUID projectId){
        if (!projectUserRepository.existsByProjectIdAndUserId(projectId, userToken)) throw new NullPointerException();
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new EntityNotFoundException("project not found"));

        return convertToDto(project);
    }
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .map(projectUser -> userRepository.findById(projectUser.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found")))
                .toList();
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            ROLE role = projectUserRepository.findByUserIdAndProjectId(user.getId(), project.getId()).getRole();
            UserDto dto = UserDto.fromEntity(user);
            dto.setRole(role);
            userDto.add(dto);
        }
        return fromEntity(project, userDto);
    }
    public void updateProject(Long userId, UUID projectId, PutProjectDto updatedProjectData) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("project not found"));
        ROLE role = projectUserRepository.findByUserIdAndProjectId(userId, projectId).getRole();

        if (role.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        project.setTitle(updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate());

        projectRepositroy.save(project);
    }
    public void deleteProject(Long token, UUID projectId) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("project not found"));

        ROLE role = projectUserRepository.findByUserIdAndProjectId(token, projectId).getRole();

        if (!role.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        }

        List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectId);
        projectUserRepository.deleteAll(projectUsers);
        projectRepositroy.delete(project);
    }
    public List<UserDto> findByAllProjectUsers(Long token, UUID projectId) {
        Project project = projectRepositroy.findById(projectId).orElseThrow();

        if (!projectUserRepository.existsByProjectIdAndUserId(projectId, token)) {
            throw new NullPointerException();
        }

        List<User> users = project.getProjectUsers().stream()
                .map(projectUser -> userRepository.findById(projectUser.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found")))
                .toList();
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            ROLE role = projectUserRepository.findByUserIdAndProjectId(user.getId(), project.getId()).getRole();
            UserDto dto = UserDto.fromEntity(user);
            dto.setRole(role);
            userDto.add(dto);
        }
        return userDto;
    }
    public void addUser(Long token, InviteDto inviteDto) {
        Project project = projectRepositroy.findById(inviteDto.getProjectId()).orElseThrow(() -> new EntityNotFoundException("not found project"));

        ROLE role = projectUserRepository.findByUserIdAndProjectId(token, project.getId()).getRole();

        if (role.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        List<String> userEmails = inviteDto.getUserEmails();
        for (String email : userEmails) {
            if (userRepository.existsByEmail(email)) {
                Long userId = userRepository.findByEmail(email).getId();
                UUID projectId = inviteDto.getProjectId();
                if (!projectUserRepository.existsByProjectIdAndUserId(projectId, userId)) {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setProjectId(projectId);
                    projectUser.setUserId(userId);
                    projectUser.setRole(ROLE.GUEST);
                    projectUser.setColor(project.getColor());

                    projectUserRepository.save(projectUser);
                }
            }
        }
    }

    public void changeRole(Long userId, RoleDto roleDto) {
        ProjectUser self = projectUserRepository.findByUserIdAndProjectId(userId, roleDto.getProjectId());

        if (self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        Map<String, ROLE> roles = roleDto.getRoles();
        for (Map.Entry<String, ROLE> entry : roles.entrySet()) {
            String email = entry.getKey();
            ROLE role = entry.getValue();

            Long uId = userRepository.findByEmail(email).getId();
            ProjectUser projectUser = projectUserRepository.findByUserIdAndProjectId(uId, roleDto.getProjectId());
            if (role.equals(ROLE.OWNER) && self.getRole().equals(ROLE.WRITER)) {
                throw new NullPointerException();
            }
            projectUser.setRole(role);

            projectUserRepository.save(projectUser);
        }
    }
    public void deleteProjectUser(Long userId, UUID projectId, List<String> userEmails) {
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new EntityNotFoundException("project not found"));
        ROLE role = projectUserRepository.findByUserIdAndProjectId(userId, projectId).getRole();

        if (!role.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        }

        List<ProjectUser> projectUsers = project.getProjectUsers();
        for (ProjectUser projectUser : projectUsers){
            for (String email : userEmails) {
                User user = userRepository.findByEmail(email);
                if (user.getId() == projectUser.getUserId()) {
                    projectUserRepository.delete(projectUser);
                }
            }
        }
    }
}
