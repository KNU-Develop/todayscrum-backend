package knu.kproject.service;

import knu.kproject.dto.AdminUserDto;
import knu.kproject.entity.User;
import knu.kproject.repository.UserRepository;
import knu.kproject.util.DtoToEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public User createUser(AdminUserDto userDto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userDto.getEmail());
        user.setOnboardingCompleted(userDto.isOnboardingCompleted());
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setTool(DtoToEntityConverter.convertToTool(userDto.getTool()));
        user.setStack(userDto.getStack());
        user.setMbti(userDto.getMbti());
        user.setProfilePicture(userDto.getProfilePicture());

        return userRepository.save(user);
    }
    public User getProjectUserData(UUID uid) {
        return userRepository.findById(uid).orElseThrow(() -> new NoSuchElementException("User is not define"));
    }
//    // 내 정보 조회
//    @Transactional(readOnly = true)
//    public User getMyInfo() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
//        UUID userId = userDetails.getUserId();
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
//    }
//
//    // 내 정보 수정
//    @Transactional
//    public User updateMyInfo(UserDto userDto) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
//        UUID userId = userDetails.getUserId();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
//
//        user.setName(userDto.getName());
//        user.setPhone(userDto.getPhone());
//        user.setAddress(userDto.getAddress());
//        user.setTool(userDto.getTool());
//        user.setStack(userDto.getStack());
//        user.setMbti(userDto.getMbti());
//        user.setProfilePicture(userDto.getProfilePicture());
//
//        return userRepository.save(user);
//    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(UUID id, AdminUserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setOnboardingCompleted(userDto.isOnboardingCompleted());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setTool(DtoToEntityConverter.convertToTool(userDto.getTool()));
        user.setStack(userDto.getStack());
        user.setMbti(userDto.getMbti());
        user.setProfilePicture(userDto.getProfilePicture());

        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

//    @Transactional(readOnly = true)
//    public List<User> getTeamMembers(int projectId) {
//        return userRepository.findTeamMembersByProjectId(projectId);
//    }
}
