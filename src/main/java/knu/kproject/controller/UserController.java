package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.JoinUserDto;
import knu.kproject.dto.UserDto.UpdateUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.UserService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @Operation(summary = "회원가입", description = "회원가입 시 필수 추가 정보를 등록 API 입니다.")
    @PostMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원가입 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    public ResponseEntity<?> join(
            @Parameter(description = "회원가입 시 필요한 추가 정보", required = true)
            @Valid @RequestBody JoinUserDto joinUserDto,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Api_Response<Boolean>> result;
        try {
            userService.joinUser(userId, joinUserDto);
            result = ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            result = ApiResponseUtil.createBadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }

    @Operation(summary = "회원 선택정보", description = "추가 정보를 등록 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 정보 등록 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "추가 정보 등록 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PostMapping("/info")
    public ResponseEntity<?> addUserInfo(
            @Parameter(description = "추가 정보 입력", required = true)
            @Valid @RequestBody AdditionalUserInfo additionalInfo,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Api_Response<Boolean>> result;
        try {
            userService.addUserInfo(userId, additionalInfo);
            result = ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            result = ApiResponseUtil.createBadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }

    @Operation(summary = "내 정보 조회", description = "User 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원정보 조회 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(
            @Parameter(description = "회원정보 조회", required = true)
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Api_Response<UserDto>> result;
        try {
            UserDto userDto = userService.getUserInfo(userId);
            result = ApiResponseUtil.createResponse(SuccessCode.SELECT_SUCCESS.getStatus(), SuccessCode.SELECT_SUCCESS.getMessage(), userDto);
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }

    @Operation(summary = "내 정보 수정", description = "User 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원정보 수정 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/info/optional")
    public ResponseEntity<?> updateUserInfo(
            @Parameter(description = "회원 정보 수정", required = true)
            @Valid @RequestBody UpdateUserDto updateUserDto,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Api_Response<Boolean>> result;
        try {
            userService.updateUserInfo(userId, updateUserDto);
            result = ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            result = ApiResponseUtil.createBadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원 탈퇴 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/withdraw")
    public ResponseEntity<?> withdrawUser(
            @Parameter(description = "회원 탈퇴", required = true)
            @Valid @RequestBody UserDto userDto,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        ResponseEntity<Api_Response<Boolean>> result;
        try {
            userService.withdraw(userId, userDto);
            result = ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            result = ApiResponseUtil.createBadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }
}