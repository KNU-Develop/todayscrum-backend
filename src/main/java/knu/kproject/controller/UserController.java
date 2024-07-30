package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.entity.User;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "User API 명세서 입니다.")
@RestController
@RequestMapping("/UserInfo")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보 확인", description = "User 조회 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "500", description = "false", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @GetMapping("")
    public ResponseEntity<?> getUserInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        try {
            User user = userService.findById(id);
            Api_Response<User> response = Api_Response.<User>builder()
                    .code(SuccessCode.SELECT_SUCCESS.getStatus())
                    .Description(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(user)
                    .build();
            return ResponseEntity.ok().body(new Api_Response<>(user, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new Api_Response<>(false, 500,"Fail"));
        }
    }
    @Operation(summary = "유저 정보 수정", description = "User 수정 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "false", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @PutMapping("")
    public ResponseEntity<?> updateUserInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long id, @RequestBody UserDto userDto) {
        try {
            User user = userService.updateMyInfo(id, userDto);
            Api_Response<User> response = Api_Response.<User>builder()
                    .code(SuccessCode.UPDATE_SUCCESS.getStatus())
                    .Description(SuccessCode.UPDATE_SUCCESS.getMessage())
                    .result(user)
                    .build();
            return ResponseEntity.ok().body(new Api_Response<>(true, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new Api_Response<>(false, 500, "Fail"));
        }
    }
//    @GetMapping("/user")
//    public ResponseEntity<?> getUserById(@RequestParam Long id) {
//        User user = userService.findById(id);
//        Api_Response<User> response = Api_Response.<User>builder()
//                .code(SuccessCode.SELECT_SUCCESS.getStatus())
//                .Description(SuccessCode.SELECT_SUCCESS.getMessage())
//                .result(user)
//                .build();
//        return ResponseEntity.ok(response);
//    }
    @Operation(summary = "유저 정보 수정", description = "User 수정 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success", content = @Content(mediaType = "application/json", schema = @Schema(type = "true"))),
                    @ApiResponse(responseCode = "500", description = "false", content = @Content(mediaType = "applicaion/json", schema = @Schema(type = "false")))
            }
    )
    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        try {
            userService.deleteUser(id);
//            Api_Response<Void> response = Api_Response.<Void>builder()
//                    .code(SuccessCode.DELETE_SUCCESS.getStatus())
//                    .Description(SuccessCode.DELETE_SUCCESS.getMessage())
//                    .build();
            return ResponseEntity.ok().body(new Api_Response<>(true, 200, "SUCCESS"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok().body(new Api_Response<>(false, 500, "Fail"));
        }
    }
}
