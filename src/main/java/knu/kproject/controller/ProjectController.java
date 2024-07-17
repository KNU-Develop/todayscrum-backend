package knu.kproject.controller;

import knu.kproject.dto.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.User;
import knu.kproject.repository.ProjectUserRepository;
import knu.kproject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("workspace/{workSpaceId}/")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

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
    public ResponseEntity<List<Project>> getProjectsByWorkspaceId(@PathVariable Long workSpaceId) {
        List<Project> projects = projectService.getProjectByWorkspaceId(workSpaceId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("projects/{ProjectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long ProjectId) {
        Optional<Project> project = projectService.getProjectById(ProjectId);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<List<String>> getProjectToUser(@PathVariable Long projectId) {
        List<ProjectUser> projectUsers = projectService.findByAllProjectUsers(projectId);
        List<String> usersId = projectUsers.stream()
                .map(ProjectUser::getUserId)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usersId);
    }
}
