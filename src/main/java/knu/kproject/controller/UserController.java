package knu.kproject.controller;

import knu.kproject.dto.UserDto.UserDto;
import knu.kproject.global.code.ApiResponse;
import knu.kproject.entity.User;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/UserInfo")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Long id) {
        User user = userService.findById(id);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .code(SuccessCode.SELECT_SUCCESS.getStatus())
                .msg(SuccessCode.SELECT_SUCCESS.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal Long id, @RequestBody UserDto userDto) {
        User user = userService.updateMyInfo(id, userDto);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .code(SuccessCode.UPDATE_SUCCESS.getStatus())
                .msg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserById(@RequestParam Long id) {
        User user = userService.findById(id);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .code(SuccessCode.SELECT_SUCCESS.getStatus())
                .msg(SuccessCode.SELECT_SUCCESS.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(SuccessCode.DELETE_SUCCESS.getStatus())
                .msg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
}
