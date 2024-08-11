package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.UserDto.ToolInfoDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.project.*;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.entity.workspace.Workspace;
import knu.kproject.global.ROLE;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepositroy projectRepositroy;
    private final ProjectUserRepository projectUserRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    public UserTeamDto fromEntity(User user, Project project) {
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        UserTeamDto dto = new UserTeamDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSocialId(user.getSocialId());
        dto.setStatus(user.getStatus());
        dto.setEmail(user.getEmail());
        dto.setContact(user.getContact());
        dto.setLocation(user.getLocation());
        dto.setMbti(user.getMbti());
        dto.setImageUrl(user.getImageUrl());
        dto.setRole(user.getRole());
        dto.setColor(projectUser.getColor());
        dto.setTools(user.getUserTools().stream()
                .map(ToolInfoDto::fromEntity)
                .collect(Collectors.toList()));
        dto.setStackNames(user.getUserStacks().stream()
                .map(userStack -> userStack.getStack().getName())
                .collect(Collectors.toList()));

        return dto;
    }

    public UUID createProject(PutProjectDto projectDto, Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByOwnerId(userId);

        if (userId == null) throw new IllegalArgumentException("illegal error");
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
                .color(project.getColor())
                .build();
        List<ProjectUser> projectUserList = new ArrayList<>();
        projectUserList.add(projectUser);

        projectUserRepository.save(projectUser);

        project.setProjectUsers(projectUserList);
        projectRepositroy.save(project);

        return project.getId();
    }

    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId) {
        if (workspaceId == null) throw new IllegalArgumentException("error");

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

    public ProjectDto getProjectById(Long userToken, UUID projectId) {
        User user = userRepository.findById(userToken).orElseThrow();
        Project project = projectRepositroy.findById(projectId).orElseThrow();
        if (!projectUserRepository.existsByProjectAndUser(project, user)) throw new NullPointerException();

        return convertToDto(project);
    }

    // 특정 프로젝트 내에 존재하는 유저 탐색해서 project내 유저로 변환
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .map(ProjectUser::getUser)
                .toList();
        List<UserTeamDto> userDto = new ArrayList<>();
        for (User user : users) {
            ROLE role = projectUserRepository.findByUserAndProject(user, project).getRole();
            UserTeamDto dto = fromEntity(user, project);
            dto.setRole(role);
            userDto.add(dto);
        }
        return ProjectDto.fromEntity(project, userDto);
    }

    public void updateProject(Long userId, UUID projectId, PutProjectDto updatedProjectData) {
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("project not found"));
        ROLE role = projectUserRepository.findByUserAndProject(userRepository.findById(userId).orElseThrow(), project).getRole();

        if (role.equals(ROLE.GUEST)) {
            throw new NullPointerException();
        }
        List<ProjectUser> projectUserList = projectUserRepository.findByProjectId(projectId);
        // 유저와 프로젝트 색이 일치할 경우, 프로젝트 색을 따름, 다를경우 개인 색 유지
        for (ProjectUser user : projectUserList) {
            if (project.getColor() == null && user.getColor() == null) {
                user.setColor(updatedProjectData.getColor());
                continue;
            } else if (project.getColor() != null && user.getColor() != null && project.getColor().equals(user.getColor())) {
                user.setColor(updatedProjectData.getColor());
            }
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
            Long id = Long.parseLong(entry.getKey());
            ROLE role = entry.getValue();

            ProjectUser projectUser = projectUserRepository.findByUserAndProject(userRepository.findById(id).orElseThrow(), project);
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
        for (ProjectUser projectUser : projectUsers) {
            for (String email : userEmails) {
                User user = userRepository.findByEmail(email);
                if (user.getId() == projectUser.getUser().getId()) {
                    projectUserRepository.delete(projectUser);
                }
            }
        }
    }
}
