package knu.kproject.controller;

import knu.kproject.dto.ProjectDto;
import knu.kproject.entity.Project;
import knu.kproject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
}
