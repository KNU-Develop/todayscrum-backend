package knu.kproject.service;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Null;
import knu.kproject.dto.UserDto.ToolInfoDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.notice.NoticeDto;
import knu.kproject.dto.project.*;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.entity.workspace.Workspace;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.CHOICE;
import knu.kproject.global.NOTICETYPE;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NoticeService noticeService;

    public UserTeamDto fromEntity(User user, Project project) {
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        UserTeamDto dto = new UserTeamDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setLocation(user.getLocation());
        dto.setMbti(user.getMbti());
        dto.setImageUrl(user.getImageUrl());
        dto.setRole(user.getRole());
        dto.setChoice(projectUser.getChoice());
        dto.setColor(projectUser.getColor());
        dto.setTools(user.getUserTools().stream()
                .map(ToolInfoDto::fromEntity)
                .collect(Collectors.toList()));
        dto.setStackNames(user.getUserStacks().stream()
                .map(userStack -> userStack.getStack().getName())
                .collect(Collectors.toList()));

        return dto;
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

    public UUID createProject(PutProjectDto projectDto, Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByOwnerId(userId);

        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));

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
                .user(user)
                .project(project)
                .role(ROLE.MASTER)
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
        if (workspaceId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        workspaceRepository.findById(workspaceId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));

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
        if (userToken == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);

        User user = userRepository.findById(userToken).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        Access.accessPossible(projectUser, ROLE.GUEST);

        return convertToDto(project);
    }

    @Transactional
    public void updateProject(Long userId, UUID projectId, PutProjectDto updatedProjectData) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User u = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(u, project);

        Access.accessPossible(projectUser, ROLE.WRITER);

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

        project.setTitle(updatedProjectData.getTitle() == null ? project.getTitle() : updatedProjectData.getTitle());
        project.setOverview(updatedProjectData.getOverview() == null ? project.getOverview() : updatedProjectData.getOverview());
        project.setStartDate(updatedProjectData.getStartDate() == null ? project.getStartDate() : updatedProjectData.getStartDate());
        project.setEndDate(updatedProjectData.getEndDate() == null ? project.getEndDate() : updatedProjectData.getEndDate());
        project.setColor(updatedProjectData.getColor() == null ? project.getColor() : updatedProjectData.getColor());

        projectRepositroy.save(project);
    }

    public void deleteProject(Long token, UUID projectId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);

        Access.accessPossible(projectUser, ROLE.OWNER);

        projectRepositroy.delete(project);
    }

    public List<UserTeamDto> findByAllProjectUsers(Long token, UUID projectId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user1 = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user1, project);
        Access.accessPossible(projectUser, ROLE.GUEST);

        List<UserTeamDto> users = project.getProjectUsers().stream()
                .map(projectUser1 -> fromEntity(projectUser1.getUser(), projectUser1.getProject()))
                .toList();
        return users;
    }

    public void addUser(Long token, UUID projectId, InviteDto inviteDto) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user1 = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser1 = projectUserRepository.findByUserAndProject(user1, project);

        Access.accessPossible(projectUser1, ROLE.GUEST);

        List<String> userEmails = inviteDto.getUserEmails();
        for (String email : userEmails) {
            if (userRepository.existsByEmail(email)) {
                User user = userRepository.findByEmail(email);
                if (!projectUserRepository.existsByProjectAndUser(project, user)) {
                    NoticeDto noticeDto = NoticeDto.builder()
                            .isRead(false)
                            .title(user1.getName() + "님이 " + project.getTitle() + "에 초대했습니다.")
                            .type(NOTICETYPE.초대)
                            .originId(project.getId())
                            .originTable("project")
                            .user(user)
                            .choice(CHOICE.전송)
                            .build();

                    ProjectUser projectUser = ProjectUser.builder()
                            .project(project)
                            .user(user)
                            .role(ROLE.GUEST)
                            .color(project.getColor())
                            .choice(CHOICE.전송)
                            .build();

                    noticeService.addNotice(user, noticeDto);
                    projectUserRepository.save(projectUser);
                }
            }
        }
    }

    @Transactional
    public void changeRole(Long userId, UUID projectId, RoleDto roleDto) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser self = projectUserRepository.findByUserAndProject(user, project);

        Access.accessPossible(self, ROLE.GUEST);

        Map<Long, ROLE> roles = roleDto.getRoles();
        for (Map.Entry<Long, ROLE> entry : roles.entrySet()) {
            Long id = entry.getKey();
            ROLE role = entry.getValue();

            ProjectUser projectUser = projectUserRepository.findByUserAndProject(userRepository.findById(id).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER)), project);
            Access.accessPossible(self, role);
            projectUser.setRole(role);

            projectUserRepository.save(projectUser);
        }
    }

    @Transactional
    public void deleteProjectUser(Long userId, UUID projectId, List<Long> UserId) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User self = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepositroy.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser1 = projectUserRepository.findByUserAndProject(self, project);
        Access.accessPossible(projectUser1, ROLE.OWNER);

        Iterator<ProjectUser> projectUserIterator = project.getProjectUsers().iterator();

        while (projectUserIterator.hasNext()) {
            ProjectUser projectUser = projectUserIterator.next();
            for (Long id : UserId) {
                User user = userRepository.findById(id).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
                if (user != null && user.equals(projectUser.getUser())) {
                    projectUserIterator.remove();
                    user.getProjectUsers().remove(projectUser);

                    projectUserRepository.delete(projectUser);
                }
            }
        }
    }
}
