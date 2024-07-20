package knu.kproject.controller;

import knu.kproject.api.ApiResponse;
import knu.kproject.dto.AdminUserDto;
import knu.kproject.dto.UserDto;
import knu.kproject.entity.User;
import knu.kproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody AdminUserDto userDto) {
        User user = userService.createUser(userDto);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", user));
    }

//    @GetMapping("/me")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ApiResponse<User>> getMyInfo() {
//        User user = userService.getMyInfo();
//        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", user));
//    }
//
//    @PutMapping("/me")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ApiResponse<User>> updateMyInfo(@RequestBody UserDto userDto) {
//        User updatedUser = userService.updateMyInfo(userDto);
//        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", updatedUser));
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable UUID id, @RequestBody AdminUserDto userDto) {
        User user = userService.updateUser(id, userDto);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable UUID id) {
        User user = userService.getProjectUserData(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", user));
    }

//    @GetMapping("/team/{projectId}")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<ApiResponse<List<User>>> getTeamMembers(@PathVariable int projectId) {
//        List<User> teamMembers = userService.getTeamMembers(projectId);
//        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", teamMembers));
//    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", users));
    }
}

//관리자 user 정보 조회
    //관리자 user list 조회
    //본인 정보 조회
    //프로젝트 팀원 정보 조회
    //회원 정보 수정
    //회원 정보 삭제
