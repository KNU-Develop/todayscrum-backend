package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Null;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.dto.project.InviteDto;
import knu.kproject.dto.project.ProjectDto;
import knu.kproject.dto.project.PutProjectDto;
import knu.kproject.dto.project.RoleDto;
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
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Project API", description = "project API 명세서 입니다.")
@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    @Operation(summary = "project 생성", description = "프로젝트 생성 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 오류"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("")
    public ResponseEntity<Api_Response<Object>> createProject(@RequestBody PutProjectDto projectDto, @AuthenticationPrincipal Long key) {
        try {
            UUID projectId = projectService.createProject(projectDto, key);
            Map<String, UUID> projectMap = new HashMap<>();
            projectMap.put("projectId", projectId);
            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(SuccessCode.INSERT_SUCCESS.getStatus())
                    .message(SuccessCode.INSERT_SUCCESS.getMessage())
                    .result(projectMap)
                    .build());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createBadRequestResponse(
                    ErrorCode.BAD_REQUEST_ERROR.getMessage()
            );
        }
    }
    @Operation(summary = "모든 프로젝트 조회", description = "특정 workspace 아래 존재하는 모든 project 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("")
    public ResponseEntity<Api_Response<Object>> getProjectsByWorkspaceId(@AuthenticationPrincipal Long key) {
        try {
            List<ProjectDto> projects = projectService.getProjectByWorkspaceId(key);

            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .message(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(projects)
                    .build());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e){
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.SELECT_ERROR.getMessage(),
                    ErrorCode.SELECT_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트 조회", description = "특정 project 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/project")
    public ResponseEntity<Api_Response<Object>> getProjectById(@AuthenticationPrincipal Long userToken , @RequestParam UUID key) {
        try {
            if (userToken == null) throw new IllegalArgumentException("Authorization error");
            ProjectDto project = projectService.getProjectById(userToken, key);
            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .message(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(project).build());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.SELECT_ERROR.getMessage(),
                    ErrorCode.SELECT_ERROR.getStatus()
            );
        }
    }

    @Operation(summary = "특정 프로젝트 수정", description = "특정 project 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 수정 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/project")
    public ResponseEntity<Api_Response<Object>> updateProject(@AuthenticationPrincipal Long userToken, @RequestParam UUID key, @RequestBody PutProjectDto updateProjectData) {
        try {
            if (userToken == null) throw new IllegalArgumentException("Authorization error");
            projectService.updateProject(userToken, key, updateProjectData);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.UPDATE_ERROR.getMessage(),
                    ErrorCode.UPDATE_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트 삭제", description = "특정 project 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/project")
    public ResponseEntity<Api_Response<Object>> deleteProject(@AuthenticationPrincipal Long token, @RequestParam UUID key) {
        try {
            if (token == null) throw new IllegalArgumentException("error");
            projectService.deleteProject(token, key);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.DELETE_ERROR.getMessage(),
                    ErrorCode.DELETE_ERROR.getStatus()
            );
        }
    }
    @Operation(summary = "특정 프로젝트의 팀원 추가", description = "특정 project의 팀원 추가 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 추가 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/project")
    public ResponseEntity<Api_Response<Object>> addUser(@AuthenticationPrincipal Long token, @RequestBody InviteDto inviteDto) {
        try {
            if (token == null) throw new IllegalArgumentException("error");
            projectService.addUser(token, inviteDto);
            return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(ErrorCode.NOT_FOUND_ERROR.getMessage());
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.INSERT_ERROR.getMessage(),
                    ErrorCode.INSERT_ERROR.getStatus());
        }
    }
    @Operation(summary = "특정 프로젝트의 팀원 조회", description = "특정 project의 팀원 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("project/users")
    public ResponseEntity<Api_Response<Object>> getProjectToUser(@AuthenticationPrincipal Long token, @RequestParam UUID key) {
        try {
            if (token == null) throw new IllegalArgumentException("error");

            List<UserDto> users = projectService.findByAllProjectUsers(token, key);

            return ResponseEntity.ok().body(Api_Response.builder()
                    .code(200).message("SUCCESS").result(users).build());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(
                    ErrorCode.NOT_FOUND_ERROR.getMessage()
            );
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.SELECT_ERROR.getMessage(),
                    ErrorCode.SELECT_ERROR.getStatus()
            );
        }

    }
    @Operation(summary = "특정 프로젝트의 팀원 삭제", description = "특정 project의 팀원 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/project/user")
    public ResponseEntity<Api_Response<Object>> deleteProjectUser(@AuthenticationPrincipal Long token, @RequestBody InviteDto inviteDto) {
        try {
            if (token == null) throw new IllegalArgumentException("error");
            projectService.deleteProjectUser(token, inviteDto.getProjectId(), inviteDto.getUserEmails());
            return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (EntityNotFoundException e) {
            return ApiResponseUtil.createNotFoundResponse(ErrorCode.NOT_FOUND_ERROR.getMessage());
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(ErrorCode.DELETE_ERROR.getMessage(), ErrorCode.DELETE_ERROR.getStatus());
        }
    }
    @Operation(summary = "특정 프로젝트의 팀원의 권한 수정", description = "특정 project의 팀원 권한 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 팀원 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/project/user")
    public ResponseEntity<Api_Response<Object>> changeRole(@AuthenticationPrincipal Long token, @RequestBody RoleDto roleDto) {
        try {
            if (token == null) throw new IllegalArgumentException();
            projectService.changeRole(token, roleDto);

            return ApiResponseUtil.createSuccessResponse(
                    SuccessCode.UPDATE_SUCCESS.getMessage()
            );
        } catch (NullPointerException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.FORBIDDEN_ERROR.getMessage(),
                    ErrorCode.FORBIDDEN_ERROR.getStatus()
            );
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.createUnAuthorization();
        } catch (RuntimeException e) {
            return ApiResponseUtil.createErrorResponse(
                    ErrorCode.UPDATE_ERROR.getMessage(),
                    ErrorCode.UPDATE_ERROR.getStatus()
            );
        }
    }
}
