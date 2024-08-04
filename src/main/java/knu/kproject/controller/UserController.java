package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Operation(summary = "회원가입", description = "회원가입 시 필수 추가 정보를 등록 API 입니다.")
    @PostMapping("")
    public ResponseEntity<?> join(
            @Parameter(description = "회원가입 시 필요한 추가 정보", required = true)
            @RequestBody AdditionalUserInfo additionalInfo,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        try {
            userService.joinUser(userId, additionalInfo);
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(SuccessCode.INSERT_SUCCESS.getStatus())
                    .result(true)
                    .message(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(ErrorCode.INSERT_ERROR.getStatus())
                    .result(false)
                    .message(ErrorCode.INSERT_ERROR.getMessage())
                    .build());
        }
    }

    @Operation(summary = "회원 선택정보", description = "추가 정보를 등록 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "FAIL",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class)))
    })
    @PostMapping("/info")
    public ResponseEntity<?> addUserInfo(
            @Parameter(description = "추가 정보 입력", required = true)
            @RequestBody AdditionalUserInfo additionalInfo,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        try {
            userService.addUserInfo(userId, additionalInfo);
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(SuccessCode.INSERT_SUCCESS.getStatus())
                    .result(true)
                    .message(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            logger.error("Error adding user info: ", e);
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(ErrorCode.INSERT_ERROR.getStatus())
                    .result(false)
                    .message(ErrorCode.INSERT_ERROR.getMessage())
                    .build());
        }
    }

    @Operation(summary = "내 정보 조회", description = "User 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "FAIL",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(
            @Parameter(description = "회원정보 조회", required = true)
            @AuthenticationPrincipal Long userId) {
        try {
            UserDto userDto = userService.getUserInfo(userId);
            return ResponseEntity.ok().body(Api_Response.<UserDto>builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .result(userDto)
                    .message(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(ErrorCode.SELECT_ERROR.getStatus())
                    .result(false)
                    .message(ErrorCode.SELECT_ERROR.getMessage())
                    .build());
        }
    }

    @Operation(summary = "내 정보 수정", description = "User 수정 API 입니다.")
    @PutMapping("/info/optional")
    public ResponseEntity<?> updateUserInfo(
            @Parameter(description = "회원 정보 수정", required = true)
            @RequestBody AdditionalUserInfo additionalInfo,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        try {
            userService.updateUserInfo(userId, additionalInfo);
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(SuccessCode.UPDATE_SUCCESS.getStatus())
                    .result(true)
                    .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(ErrorCode.UPDATE_ERROR.getStatus())
                    .result(false)
                    .message(ErrorCode.UPDATE_ERROR.getMessage())
                    .build());
        }
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "FAIL",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/withdraw")
    public ResponseEntity<?> withdrawUser(
            @Parameter(description = "회원 탈퇴", required = true)
            @RequestBody UserDto userDto,
            @Parameter(description = "현재 인증된 사용자의 ID", required = true)
            @AuthenticationPrincipal Long userId) {
        try {
            userService.withdraw(userId, userDto);
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(SuccessCode.UPDATE_SUCCESS.getStatus())
                    .result(true)
                    .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(Api_Response.<Boolean>builder()
                    .code(ErrorCode.UPDATE_ERROR.getStatus())
                    .result(false)
                    .message(ErrorCode.UPDATE_ERROR.getMessage())
                    .build());
        }
    }
}