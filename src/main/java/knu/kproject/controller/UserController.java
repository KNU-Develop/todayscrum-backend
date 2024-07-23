package knu.kproject.controller;

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

    @GetMapping
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Long id) {
        User findUser = userService.findById(id);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .code(SuccessCode.SELECT_SUCCESS.getStatus())
                .msg(SuccessCode.SELECT_SUCCESS.getMessage())
                .result(findUser)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
