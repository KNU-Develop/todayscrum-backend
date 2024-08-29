package knu.kproject.service;

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
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final NoticeService noticeService;

    // 특정 프로젝트 내에 존재하는 유저 탐색해서 project내 유저로 변환
    private ProjectDto convertToDto(Project project) {
        List<User> users = project.getProjectUsers().stream()
                .filter(projectUser -> {
                    if (projectUser.getChoice() != null && projectUser.getChoice().equals(CHOICE.수락)) return true;
                    return false;
                })
                .map(ProjectUser::getUser)
                .toList();
        List<UserTeamDto> userDto = new ArrayList<>();
        for (User user : users) {
            ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);
            UserTeamDto dto = UserTeamDto.fromEntity(projectUser);
            dto.setRole(projectUser.getRole());
            userDto.add(dto);
        }
        return ProjectDto.fromEntity(project, userDto);
    }

    public UUID createProject(PutProjectDto projectDto, Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByOwnerId(userId);

        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = new Project(projectDto, workspaces.get(0));

        projectRepository.save(project);
        ProjectUser projectUser = new ProjectUser(user, project, ROLE.MASTER, CHOICE.수락);
        List<ProjectUser> projectUserList = new ArrayList<>();
        projectUserList.add(projectUser);
        projectUserRepository.save(projectUser);
        project.setProjectUsers(projectUserList);
        projectRepository.save(project);
        return project.getId();
    }

    public List<ProjectDto> getProjectByWorkspaceId(Long workspaceId) {
        if (workspaceId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        workspaceRepository.findById(workspaceId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));

        List<Project> projects = projectRepository.findByWorkspaceOwnerId(workspaceId);
        List<ProjectUser> subProjects = projectUserRepository.findByUserId(workspaceId);
        for (ProjectUser projectUser : subProjects) {
            if (!projects.contains(projectUser.getProject()) && projectUser.getChoice().equals(CHOICE.수락)) {
                projects.add(projectUser.getProject());
            }
        }

        return projects.stream().map(this::convertToDto).toList();
    }

    public ProjectDto getProjectById(Long userToken, UUID projectId) {
        if (userToken == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(userToken).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);
        return convertToDto(project);
    }

    @Transactional
    public void updateProject(Long userId, UUID projectId, PutProjectDto updatedProjectData) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User u = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId)
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
        project.update(updatedProjectData);
        projectRepository.save(project);
    }

    public void deleteProject(Long token, UUID projectId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);
        Access.accessPossible(projectUser, ROLE.OWNER);
        projectRepository.delete(project);
    }

    public List<UserTeamDto> findByAllProjectUsers(Long token, UUID projectId) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user1 = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user1, project);
        Access.accessPossible(projectUser, ROLE.GUEST);

        List<UserTeamDto> users = project.getProjectUsers().stream()
                .map(projectUser1 -> UserTeamDto.fromEntity(projectUser1))
                .toList();
        return users;
    }

    public void addUser(Long token, UUID projectId, InviteDto inviteDto) {
        if (token == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user1 = userRepository.findById(token).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser1 = projectUserRepository.findByUserAndProject(user1, project);
        Access.accessPossible(projectUser1, ROLE.GUEST);

        if (userRepository.existsByEmail(inviteDto.getEmail())) {
            User user = userRepository.findByEmail(inviteDto.getEmail());
            if (!projectUserRepository.existsByProjectAndUser(project, user)) {
                ProjectUser projectUser = new ProjectUser(user, project, ROLE.WRITER, CHOICE.전송);
                noticeService.addNotice(user, new NoticeDto(user1, user, project));
                projectUserRepository.save(projectUser);
            }
        } else {
            throw new ProjectException(ProjectErrorCode.NOT_FOUND_USER);
        }
    }

    @Transactional
    public void changeRole(Long userId, UUID projectId, RoleDto roleDto) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
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
    public void deleteProjectUser(Long userId, UUID projectId, InviteDto deleteDto) {
        if (userId == null) throw new ProjectException(ProjectErrorCode.BAD_AUTHORIZATION);
        User self = userRepository.findById(userId).orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser1 = projectUserRepository.findByUserAndProject(self, project);
        Access.accessPossible(projectUser1, ROLE.OWNER);


        List<ProjectUser> projectUserList = project.getProjectUsers();
        User user = userRepository.findByEmail(deleteDto.getEmail());
        Iterator<ProjectUser> iterator = projectUserList.iterator();

        while (iterator.hasNext()) {
            ProjectUser projectUser = iterator.next();
            if (projectUser.getUser().equals(user)) {
                iterator.remove();
                projectUserRepository.delete(projectUser);
            }
        }
    }
}
