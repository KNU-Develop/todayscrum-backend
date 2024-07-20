package knu.kproject.controller;

import knu.kproject.api.ApiResponse;
import knu.kproject.dto.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.User;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.service.ProjectService;
import knu.kproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("workspace/{workSpaceId}/")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createProject(@RequestBody ProjectDto projectDto) {
        try {
            Project project = projectService.createProject(projectDto);
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("projects")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByWorkspaceId(@PathVariable Long workSpaceId) {
        List<Project> projects = projectService.getProjectByWorkspaceId(workSpaceId);
        ApiResponse<List<Project>> response = new ApiResponse<>("SUCCESS", projects);
        return ResponseEntity.ok(response);

    }

    @GetMapping("projects/{ProjectId}")
    public ResponseEntity<ApiResponse<Optional<Project>>> getProjectById(@PathVariable Long ProjectId) {
        Optional<Project> project = projectService.getProjectById(ProjectId);
//        return project.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
        ApiResponse<Optional<Project>> response = new ApiResponse<>("SUCCESS", project);
        return ResponseEntity.ok(response);
    }

    @PutMapping("projects/{projectId}/update")
    public ResponseEntity<Project> updateProject(@PathVariable Long projectId, @RequestBody Project updateProjectData) {
        try {
            Project updatedProject = projectService.updateProject(projectId, updateProjectData);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("projects/{projectId}/delete")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok("Project delete success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("projects/{projectId}/usersAdd")
    public ProjectUser addUser(@RequestBody User user, @PathVariable Long projectId) {
        return projectService.addUser(projectId, user.getId());
    }
    @GetMapping("projects/{projectId}/users")
    public ResponseEntity<ApiResponse<List<User>>> getProjectToUser(@PathVariable Long projectId) {
        List<ProjectUser> projectUsers = projectService.findByAllProjectUsers(projectId);
        List<UUID> usersId = projectUsers.stream()
                .map(ProjectUser::getUserId)
                .sorted()
                .collect(Collectors.toList());
        List<User> users = usersId.stream()
                .map(userService::getProjectUserData)
                .collect(Collectors.toList());

        ApiResponse<List<User>> response = new ApiResponse<>("SUCCESS", users);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("projects/{projectId}/users/{userid}")
    public ResponseEntity<String> deleteProjectUser(@PathVariable UUID userid) {
        try {
            projectService.deleteProjectUser(userid);
            return ResponseEntity.ok("project user delete success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
