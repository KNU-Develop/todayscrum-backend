package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.project.InviteDto;
import knu.kproject.dto.project.ProjectDto;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.dto.workspace.WorkSpaceDto;
import knu.kproject.entity.User;
import knu.kproject.global.code.Api_Response;
import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.ProjectService;
import knu.kproject.service.UserService;
import knu.kproject.util.ApiResponseUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "workspace API", description = "workspace API 명세서 입니다.")
@RestController
@RequestMapping("workspace")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    @Operation(summary = "project 생성", description = "프로젝트 생성 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @PostMapping("")
    public ResponseEntity<Api_Response<Object>> createProject(@RequestBody PutProjectDto projectDto, @RequestParam Long workspaceId) {
        try {
            projectService.createProject(projectDto, workspaceId);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createSuccessResponse(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
    @Operation(summary = "모든 프로젝트 조회", description = "특정 workspace 아래 존재하는 모든 project 조회 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkSpaceDto.class))),
                    @ApiResponse(responseCode = "201", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "empty"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "fail")))
            }
    )
    @GetMapping("")
    public ResponseEntity<Api_Response<Object>> getProjectsByWorkspaceId(@RequestParam Long workspaceId) {
        try {
            List<ProjectDto> projects = projectService.getProjectByWorkspaceId(workspaceId);

            if (projects.isEmpty()) {
                return ApiResponseUtil.createNotFoundResponse(ErrorCode.NOT_VALID_ERROR.getMessage());
            }
            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(SuccessCode.INSERT_SUCCESS.getStatus())
                    .result(projects)
                    .message(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.SELECT_ERROR.getMessage(),
                    ErrorCode.SELECT_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트 조회", description = "특정 project 조회 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDto.class))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @GetMapping("/project")
    public ResponseEntity<Api_Response<Object>> getProjectById(@RequestParam Long projectId) {
        try {
            ProjectDto project = projectService.getProjectById(projectId);
            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(200).message("SUCCESS").result(project).build());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createNotFoundResponse(ErrorCode.SELECT_ERROR.getMessage());
        }
    }

    @Operation(summary = "특정 프로젝트 수정", description = "특정 project 수정 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @PutMapping("/project")
    public ResponseEntity<Api_Response<Object>> updateProject(@RequestParam Long projectId, @RequestBody PutProjectDto updateProjectData) {
        try {
            projectService.updateProject(projectId, updateProjectData);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.UPDATE_ERROR.getMessage(),
                    ErrorCode.UPDATE_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트 삭제", description = "특정 project 삭제 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @DeleteMapping("/project")
    public ResponseEntity<Api_Response<Object>> deleteProject(@RequestParam Long projectId) {
        try {
            projectService.deleteProject(projectId);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.DELETE_ERROR.getMessage(),
                    ErrorCode.DELETE_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트의 팀원 추가", description = "특정 project의 팀원 추가 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "String"))),
                    @ApiResponse(responseCode = "201", description = "can't", content = @Content(mediaType = "application/json", schema = @Schema(type = "String"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "String")))
            }
    )
    @PostMapping("/project")
    public ResponseEntity<Api_Response<Object>> addUser(@RequestBody InviteDto inviteDto) {
        try {
            projectService.addUser(inviteDto);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createNotFoundResponse(ErrorCode.NOT_FOUND_ERROR.getMessage());
        }
    }
    @Operation(summary = "특정 프로젝트의 팀원 조회", description = "특정 project의 팀원 조회 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "String")))
            }
    )
    @GetMapping("project/users")
    public ResponseEntity<Api_Response<Object>> getProjectToUser(@RequestParam Long projectId) {
        try {
            List<ProjectUser> projectUsers = projectService.findByAllProjectUsers(projectId);
            List<Long> usersId = projectUsers.stream()
                    .map(ProjectUser::getUserId)
                    .sorted()
                    .toList();
            if (usersId.isEmpty()) {
                return ApiResponseUtil.createNotFoundResponse(
                        ErrorCode.NOT_FOUND_ERROR.getMessage()
                );
            }
            List<UserDto> users = usersId.stream()
                    .map(userService::getUserInfo)
                    .toList();
            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(200).message("SUCCESS").result(users).build());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage(),
                    ErrorCode.NOT_FOUND_ERROR.getStatus()
            );
        }

    }
    @Operation(summary = "특정 프로젝트의 팀원 삭제", description = "특정 project의 팀원 삭제 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "fail", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @DeleteMapping("/project/user")
    public ResponseEntity<Api_Response<Object>> deleteProjectUser(@RequestParam Long projectId, @RequestParam String userName) {
        try {
            projectService.deleteProjectUser(projectId, userName);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseUtil.createNotFoundResponse(ErrorCode.DELETE_ERROR.getMessage());
        }
    }
}
