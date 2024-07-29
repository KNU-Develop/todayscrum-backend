package knu.kproject.controller;

import knu.kproject.dto.workspace.WorkSpaceDto;
import knu.kproject.entity.User;
import knu.kproject.entity.Workspace;
import knu.kproject.global.code.ApiResponse;
import knu.kproject.service.UserService;
import knu.kproject.service.WorkspaceService;
import lombok.NoArgsConstructor;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@NoArgsConstructor
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createWorkSpace(@RequestBody WorkSpaceDto workSpaceDto, @AuthenticationPrincipal Long id) {
        try {
            Workspace workspace =  workspaceService.createWorkSpace(workSpaceDto, id);
            return ResponseEntity.ok(new ApiResponse<>(workspace, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new ApiResponse<>(false, 500, "Fail"));
        }
    }
}
