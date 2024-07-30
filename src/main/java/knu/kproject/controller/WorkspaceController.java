package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.workspace.PutworkspaceDto;
import knu.kproject.dto.workspace.WorkSpaceDto;
import knu.kproject.entity.Workspace;
import knu.kproject.global.code.Api_Response;
import knu.kproject.service.UserService;
import knu.kproject.service.WorkspaceService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="WorkSapce API", description = "WorkSapce API 명세서 입니다.")
@RestController
@RequestMapping("/")
@NoArgsConstructor
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private UserService userService;

    @Operation(summary = "workspace 생성", description = "워크스페이스 생성 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "false")
            }
    )
    @PostMapping("")
    public ResponseEntity<Api_Response<?>> createWorkSpace(@RequestBody PutworkspaceDto workSpaceDto, @Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        try {
            Workspace workspace = workspaceService.createWorkSpace(workSpaceDto, id);
            return ResponseEntity.ok(new Api_Response<>(workspace, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new Api_Response<>(false, 500, "Fail"));
        }
    }
}
