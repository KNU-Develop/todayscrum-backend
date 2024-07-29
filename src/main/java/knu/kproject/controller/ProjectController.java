package knu.kproject.controller;

import knu.kproject.dto.project.ProjectDto;
import knu.kproject.global.code.ApiResponse;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.service.ProjectService;
import knu.kproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("workspace")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createProject(@RequestBody ProjectDto projectDto, @RequestParam Long workspaceId) {
        try {
            Project project = projectService.createProject(projectDto, workspaceId);
            return ResponseEntity.ok().body(new ApiResponse<>(project, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new ApiResponse<>("error", 500, "Fail"));
        }
    }
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getProjectsByWorkspaceId(@RequestParam Long workspaceId) {
        List<ProjectDto> projects = projectService.getProjectByWorkspaceId(workspaceId);

        if (projects.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse<>("empty", 201, "SUCCESS"));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(projects, 200, "SUCCESS"));
    }
    @GetMapping("project")
    public ResponseEntity<ApiResponse<?>> getProjectById(@RequestParam Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);

        if (project.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse<>("empty", 201, "SUCCESS"));
        }
        return ResponseEntity.ok().body(new ApiResponse<>(project, 200, "SUCCESS"));
    }

    @PutMapping("project")
    public ResponseEntity<ApiResponse<?>> updateProject(@RequestParam Long projectId, @RequestBody Project updateProjectData) {
        try {
            Project updatedProject = projectService.updateProject(projectId, updateProjectData);
            ApiResponse<Project> response = new ApiResponse<>(updatedProject, 200, "SUCCESS");
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new ApiResponse<>("error", 500, "Fail"));
        }
    }

    @DeleteMapping("project")
    public ResponseEntity<ApiResponse<?>> deleteProject(@RequestParam Long projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.ok().body(new ApiResponse<>(true, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new ApiResponse<>(false, 500, "Fail"));
        }
    }
    @PostMapping("project")
    public ResponseEntity<ApiResponse<?>> addUser(@RequestParam ProjectUser user, @RequestParam Long projectId) {
        String response = projectService.addUser(projectId, user.getUserId());
        if ( response.equals("success") ){
            return ResponseEntity.ok().body(new ApiResponse<>(response, 200, "SUCCESS"));
        } else if (response.equals("exist user")) {
            return ResponseEntity.ok().body(new ApiResponse<>(response, 201, "Can't"));
        } else {
            return ResponseEntity.ok().body(new ApiResponse<>(null, 500, "Fail"));
        }
    }
    // User 정보 조회가 완료되면 수정하면 됨
//    @GetMapping("projects/{projectId}/users")
//    public ResponseEntity<ApiResponse<List<User>>> getProjectToUser(@PathVariable Long projectId) {
//        List<ProjectUser> projectUsers = projectService.findByAllProjectUsers(projectId);
//        List<Long> usersId = projectUsers.stream()
//                .map(ProjectUser::getUserId)
//                .sorted()
//                .toList();
//        List<User> users = usersId.stream()
//                .map(userId -> userService.findById(userId).orElse(null))
//        ApiResponse<List<User>> response = new ApiResponse<>(users, 200, "SUCCESS");
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("project/user")
    public ResponseEntity<ApiResponse<Boolean>> deleteProjectUser(@RequestParam Long userId) {
        try {
            projectService.deleteProjectUser(userId);
            ApiResponse<Boolean> response = new ApiResponse<>(true, 200, "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<Boolean> response = new ApiResponse<>(false, 500, "Fail");
            return ResponseEntity.ok(response);
        }
    }
}
