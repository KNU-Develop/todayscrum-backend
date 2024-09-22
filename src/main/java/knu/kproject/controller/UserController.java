package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knu.kproject.dto.UserDto.AdditionalUserInfo;
import knu.kproject.dto.UserDto.JoinUserDto;
import knu.kproject.dto.UserDto.UpdateUserDto;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.code.Api_Response;
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
    public ResponseEntity<Api_Response<JoinUserDto>> join(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody JoinUserDto joinUserDto) {
        userService.joinInfo(userId, joinUserDto);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.INSERT_SUCCESS.getMessage());
    }

    @Operation(summary = "회원 선택정보", description = "추가 정보를 등록하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 정보 등록 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PostMapping("/info")
    public ResponseEntity<Api_Response<AdditionalUserInfo>> addUserInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AdditionalUserInfo additionalUserInfo) {
        userService.addUserInfo(userId, additionalUserInfo);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
    }

    @Operation(summary = "내 정보 조회", description = "User 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원정보 조회 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<Api_Response<UserDto>> getUserInfo(
            @AuthenticationPrincipal Long userId) {
        UserDto userDto = userService.getUserInfo(userId);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                userDto);
    }

    @Operation(summary = "내 정보 수정", description = "User 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 수정 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원정보 수정 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/info/optional")
    public ResponseEntity<Api_Response<AdditionalUserInfo>> updateUserInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        userService.updateUserInfo(userId, updateUserDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
    }
    @Operation(summary = "내 일정 색상 수정", description = "User 색상 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "색상 수정 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "색상 수정 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/color")
    public ResponseEntity<Api_Response<UserDto>> updateUserColor(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        userService.updateUserColor(userId, updateUserDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "404", description = "회원 탈퇴 실패", content = @Content(schema = @Schema(implementation = Api_Response.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Api_Response.class)))
    })
    @PutMapping("/withdraw")
    public ResponseEntity<Api_Response<UserDto>> withdrawUser(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserDto userDto) {
        userService.withdraw(userId, userDto);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage());
    }
}