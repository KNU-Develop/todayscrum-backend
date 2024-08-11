package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.workspace.PutworkspaceDto;
import knu.kproject.entity.workspace.Workspace;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.UserService;
import knu.kproject.service.WorkspaceService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WorkSapce API", description = "WorkSapce API 명세서 입니다.")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final UserService userService;

    @Operation(summary = "workspace 생성", description = "워크스페이스 생성 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "false")
            }
    )
    @PostMapping("")
    public ResponseEntity<Api_Response<Workspace>> createWorkSpace(@RequestBody PutworkspaceDto workSpaceDto, @Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        try {
            Workspace workspace = workspaceService.createWorkSpace(workSpaceDto, id);
            return ApiResponseUtil.createResponse(
                    SuccessCode.INSERT_SUCCESS.getStatus(),
                    SuccessCode.INSERT_SUCCESS.getMessage(),
                    workspace
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
    }
}
