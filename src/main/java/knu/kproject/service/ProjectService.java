package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.dto.project.InviteDto;
import knu.kproject.dto.project.ProjectDto;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.dto.project.RoleDto;
import knu.kproject.entity.*;
import knu.kproject.global.ROLE;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
//        List<Board> boards = boardRepository.findByProject(project);
//        project.setBoards(boards);

        ProjectDto dto = ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .overview(project.getOverview())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .workspaceId(project.getWorkspace().getId())
                .users(users)
                .boards(project.getBoards().stream()
                    .map(BoardDto::fromEntity)
                    .toList())
                .build();

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
                .user(userRepository.findById(userId).orElseThrow())
                .project(project)
                .role(ROLE.OWNER)
                .color(projectDto.getColor())
                .build();
        List<ProjectUser> projectUserList = new ArrayList<>();
        projectUserList.add(projectUser);

        projectUserRepository.save(projectUser);

        project.setProjectUsers(projectUserList);
        projectRepositroy.save(project);

        return project.getId();
    }
    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId){
        if (workspaceId==null) throw new IllegalArgumentException("error");

        workspaceRepository.findById(workspaceId).orElseThrow(() -> new EntityNotFoundException("workspace not found"));
        List<Project> projects = projectRepositroy.findByWorkspaceOwnerId(workspaceId);
        List<ProjectUser> subProjects = projectUserRepository.findByUserId(workspaceId);
        for (ProjectUser projectUser : subProjects) {
            if (!projects.contains(projectUser.getProject())) {
                projects.add(projectUser.getProject());
            }
        }

        return projects.stream().map(this::convertToDto).toList();
    }
    public ProjectDto getProjectById(Long userToken, UUID projectId){
        User user = userRepository.findById(userToken).orElseThrow();
        Project project = projectRepositroy.findById(projectId).orElseThrow();
        if (!projectUserRepository.existsByProjectAndUser(project, user)) throw new NullPointerException();

        return convertToDto(project);
    }
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .map(ProjectUser::getUser)
                .toList();
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            ROLE role = projectUserRepository.findByUserAndProject(user, project).getRole();
            UserDto dto = UserDto.fromEntity(user);
            dto.setRole(role);
            userDto.add(dto);
        }
        return fromEntity(project, userDto);
    }
    public void updateProject(Long userId, UUID projectId, PutProjectDto updatedProjectData) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("project not found"));
        ROLE role = projectUserRepository.findByUserAndProject(userRepository.findById(userId).orElseThrow(), project).getRole();

        if (role.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        project.setTitle(updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate());
        project.setColor(updatedProjectData.getColor());

        projectRepositroy.save(project);
    }
    public void deleteProject(Long token, UUID projectId) {
        User user = userRepository.findById(token).orElseThrow();
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("project not found"));

        ROLE role = projectUserRepository.findByUserAndProject(user, project).getRole();

        if (!role.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        }

        projectRepositroy.delete(project);
    }
    public List<UserDto> findByAllProjectUsers(Long token, UUID projectId) {
        User user1 = userRepository.findById(token).orElseThrow();
        Project project = projectRepositroy.findById(projectId).orElseThrow();

        if (!projectUserRepository.existsByProjectAndUser(project, user1)) {
            throw new NullPointerException();
        }

        List<User> users = project.getProjectUsers().stream()
                .map(ProjectUser::getUser)
                .toList();
        List<UserDto> userDto = new ArrayList<>();
        for (User user : users) {
            ROLE role = projectUserRepository.findByUserAndProject(user, project).getRole();
            UserDto dto = UserDto.fromEntity(user);
            dto.setRole(role);
            userDto.add(dto);
        }
        return userDto;
    }
    public void addUser(Long token, InviteDto inviteDto) {
        User user1 = userRepository.findById(token).orElseThrow();
        Project project = projectRepositroy.findById(inviteDto.getProjectId()).orElseThrow(() -> new EntityNotFoundException("not found project"));

        ROLE role = projectUserRepository.findByUserAndProject(user1, project).getRole();

        if (role.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        List<String> userEmails = inviteDto.getUserEmails();
        for (String email : userEmails) {
            if (userRepository.existsByEmail(email)) {
                User user = userRepository.findByEmail(email);
                if (!projectUserRepository.existsByProjectAndUser(project, user)) {
                    ProjectUser projectUser = new ProjectUser();
                    projectUser.setProject(project);
                    projectUser.setUser(user);
                    projectUser.setRole(ROLE.GUEST);
                    projectUser.setColor(project.getColor());

                    projectUserRepository.save(projectUser);
                }
            }
        }
    }

    public void changeRole(Long userId, RoleDto roleDto) {
        User user = userRepository.findById(userId).orElseThrow();
        Project project = projectRepositroy.findById(roleDto.getProjectId()).orElseThrow();

        ProjectUser self = projectUserRepository.findByUserAndProject(user, project);

        if (self.getRole().equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }

        Map<String, ROLE> roles = roleDto.getRoles();
        for (Map.Entry<String, ROLE> entry : roles.entrySet()) {
            String email = entry.getKey();
            ROLE role = entry.getValue();

            Long uId = userRepository.findByEmail(email).getId();
            ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);
            if (role.equals(ROLE.OWNER) && self.getRole().equals(ROLE.WRITER)) {
                throw new NullPointerException();
            }
            projectUser.setRole(role);

            projectUserRepository.save(projectUser);
        }
    }
    public void deleteProjectUser(Long userId, UUID projectId, List<String> userEmails) {
        User self = userRepository.findById(userId).orElseThrow();
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new EntityNotFoundException("project not found"));
        ROLE role = projectUserRepository.findByUserAndProject(self, project).getRole();

        if (!role.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        }

        List<ProjectUser> projectUsers = project.getProjectUsers();
        for (ProjectUser projectUser : projectUsers){
            for (String email : userEmails) {
                User user = userRepository.findByEmail(email);
                if (user.getId() == projectUser.getUser().getId()) {
                    projectUserRepository.delete(projectUser);
                }
            }
        }
    }
}
