package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.kproject.dto.project.*;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.ProjectService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Project API", description = "project API 명세서 입니다.")
@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "project 생성", description = "프로젝트 생성 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorization 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("project")
    public ResponseEntity<Api_Response<Object>> createProject(@Valid @RequestBody PutProjectDto projectDto, @AuthenticationPrincipal Long key) {
        UUID projectId = projectService.createProject(projectDto, key);
        Map<String, UUID> projectMap = new HashMap<>();
        projectMap.put("projectId", projectId);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.INSERT_SUCCESS.getMessage(),
                projectMap
        );
    }

    @Operation(summary = "모든 프로젝트 조회", description = "특정 workspace 아래 존재하는 모든 project 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("project")
    public ResponseEntity<Api_Response<Object>> getProjectsByWorkspaceId(@AuthenticationPrincipal Long key) {
        List<ProjectDto> projects = projectService.getProjectByWorkspaceId(key);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                projects
        );
    }

    @Operation(summary = "특정 프로젝트 조회", description = "특정 project 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Api_Response<Object>> getProjectById(@AuthenticationPrincipal Long userToken, @PathVariable UUID projectId) {
        ProjectDto project = projectService.getProjectById(userToken, projectId);
        return ResponseEntity.ok().body(Api_Response.builder()
                .code(SuccessCode.SELECT_SUCCESS.getStatus())
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .result(project).build());
    }

    @Operation(summary = "특정 프로젝트 수정", description = "특정 project 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/project/{projectId}")
    public ResponseEntity<Api_Response<Object>> updateProject(@AuthenticationPrincipal Long userToken, @PathVariable UUID projectId, @Valid @RequestBody PutProjectDto updateProjectData) {
        projectService.updateProject(userToken, projectId, updateProjectData);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "특정 프로젝트 삭제", description = "특정 project 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Api_Response<Object>> deleteProject(@AuthenticationPrincipal Long token, @PathVariable UUID projectId) {
        projectService.deleteProject(token, projectId);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
    }

    @Operation(summary = "특정 프로젝트의 팀원 추가", description = "특정 project의 팀원 추가 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/project/{projectId}")
    public ResponseEntity<Api_Response<Object>> addUser(@AuthenticationPrincipal Long token, @PathVariable UUID projectId, @Valid @RequestBody InviteDto inviteDto) {
        projectService.addUser(token, projectId, inviteDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
    }

    @Operation(summary = "특정 프로젝트의 팀원 조회", description = "특정 project의 팀원 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @GetMapping("project/{projectId}/users")
    public ResponseEntity<Api_Response<Object>> getProjectToUser(@AuthenticationPrincipal Long token, @PathVariable UUID projectId) {
        List<UserTeamDto> users = projectService.findByAllProjectUsers(token, projectId);

        return ResponseEntity.ok().body(Api_Response.builder()
                .code(200).message("SUCCESS").result(users).build());
    }

    @Operation(summary = "특정 프로젝트의 팀원 삭제", description = "특정 project의 팀원 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/project/{projectId}/user")
    public ResponseEntity<Api_Response<Object>> deleteProjectUser(@AuthenticationPrincipal Long token, @PathVariable UUID projectId, @Valid @RequestBody DeleteId deleteId) {
        projectService.deleteProjectUser(token, projectId, deleteId.getUserId());
        return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
    }

    @Operation(summary = "특정 프로젝트의 팀원의 권한 수정", description = "특정 project의 팀원 권한 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/project/{projectId}/user")
    public ResponseEntity<Api_Response<Object>> changeRole(@AuthenticationPrincipal Long token, @PathVariable UUID projectId, @Valid @RequestBody RoleDto roleDto) {
        projectService.changeRole(token, projectId, roleDto);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }
}
