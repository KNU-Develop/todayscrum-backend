package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.UserDto.UserHeadDto;
import knu.kproject.dto.schedule.ScheduleCreateResultDto;
import knu.kproject.dto.schedule.ScheduleDetailResDto;
import knu.kproject.dto.schedule.ScheduleReqDto;
import knu.kproject.dto.schedule.ScheduleHeadResDto;
import knu.kproject.entity.Schedule;
import knu.kproject.entity.User;
import knu.kproject.entity.UserSchedule;
import knu.kproject.repository.ScheduleRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static knu.kproject.global.ScheduleRole.*;


@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;

    public ScheduleCreateResultDto createSchedule(Long userId, ScheduleReqDto scheduleReqDto) {
        Schedule newSchedule = scheduleRepository.save(new Schedule(scheduleReqDto));
        List<Long> notFoundUserIds = null;
        if (scheduleReqDto.getProjectId() == null) {
            inviteUser(newSchedule, userId);
        } else {
            notFoundUserIds = inviteUsersToProjectSchedule(newSchedule, scheduleReqDto.getInviteList());
        }
        return new ScheduleCreateResultDto(newSchedule.getId(), notFoundUserIds);
    }


    private boolean inviteUser(Schedule schedule, Long userId) {
        Optional<User> findUserOpt = userRepository.findById(userId);
        if (findUserOpt.isPresent()) {
            UserSchedule userSchedule = new UserSchedule(findUserOpt.get(), schedule);
            userScheduleRepository.save(userSchedule);
            return true;
        }
        return false;
    }

    private List<Long> inviteUsersToProjectSchedule(Schedule schedule, List<Long> inviteList) {
        List<Long> notFoundUserList = new ArrayList<>();
        for (Long userId : inviteList) {
            if (!inviteUser(schedule, userId))
                notFoundUserList.add(userId);
        }
        return notFoundUserList;
    }

    public List<ScheduleHeadResDto> getScheduleList(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Schedule> schedules = scheduleRepository.findScheduleList(userId, startDate, endDate)
                .orElse(Collections.emptyList());
        return schedules.stream().map(ScheduleHeadResDto::new).collect(Collectors.toList());
    }

    public ScheduleDetailResDto getScheduleDetail(Long userId, Long scheduleId) {
        Schedule findSchedule = userScheduleRepository.findScheduleByUserIdAndScheduleId(userId, scheduleId)
                .orElseThrow(EntityNotFoundException::new); // 404

        List<UserHeadDto> userDtos = null;

        if (findSchedule.getProjectId() != null) {
            userDtos = findSchedule.getUserSchedules().stream()
                    .map(userSchedule -> new UserHeadDto(userSchedule.getUser()))
                    .toList();
        }
        return new ScheduleDetailResDto(findSchedule, findSchedule.getProjectId(), userDtos);
    }


    public void updateSchedule(Long userId, Long scheduleId, ScheduleReqDto scheduleReqDto) {
        UserSchedule findUserSchedule = userScheduleRepository.findUserScheduleByUser_IdAndSchedule_Id(userId, scheduleId)
                .orElseThrow(NullPointerException::new); // 403

        if (haveChangeableRole(findUserSchedule)) {

            Schedule prevSchedule = scheduleRepository.findById(scheduleId).orElseThrow(EntityNotFoundException::new); // 404

            // 팀 프로젝트 일정 참여자 수정
            List<UserSchedule> prevUserSchedules = prevSchedule.getUserSchedules();
            List<User> prevInviteUsers = new ArrayList<>();
            for (UserSchedule prevUserSchedule : prevUserSchedules) {
                prevInviteUsers.add(prevUserSchedule.getUser());
            }
            updateUserSchedules(findUserSchedule, prevSchedule, scheduleReqDto);


        } else {
            throw new NullPointerException(); // 403
        }

    }
    private void updateUserSchedules(UserSchedule prevUserSchedule, Schedule prevSchedule, ScheduleReqDto scheduleReqDto) {
        List<User> prevInviteUsers = prevSchedule.getUserSchedules().stream()
                .map(UserSchedule::getUser).toList();
        List<Long> newInviteUserIds = scheduleReqDto.getInviteList();
        
        // 초대받지 못한 기존 참여자 제거 
        for (User prevInviteUser : prevInviteUsers) {
            if (!newInviteUserIds.contains(prevInviteUser.getId())) {
                userScheduleRepository.deleteUserScheduleByUserAndSchedule(prevInviteUser, prevSchedule);
                prevInviteUser.removeUserSchedule(prevUserSchedule);
                prevSchedule.removeUserSchedule(prevUserSchedule);
            }
        }
        // 초대 안된 새로운 사용자 추가
        for (Long newInviteUserId : newInviteUserIds) {
            if (prevInviteUsers.stream().noneMatch(user -> user.getId().equals(newInviteUserId))) {
                User newUser = userRepository.findById(newInviteUserId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + newInviteUserId));
                UserSchedule newUserSchedule = new UserSchedule();
                newUserSchedule.setUser(newUser);
                newUserSchedule.setSchedule(prevSchedule);
                newUserSchedule.setRole(WRITE);

                prevSchedule.getUserSchedules().add(newUserSchedule);
                newUser.getUserSchedules().add(newUserSchedule);

                userScheduleRepository.save(newUserSchedule);
            }
        }

    }

    private Boolean haveChangeableRole(UserSchedule userSchedule) {
        return switch (userSchedule.getRole()) {
            case OWNER, WRITE -> true;
            default -> false;
        };
    }
/*
    public void deleteScheduleById(Long scheduleId, ScheduleType type) {dis
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with ID: " + scheduleId));

        switch (type) {
            case DELETE_THIS:
                scheduleRepository.delete(schedule);
                break;
//            case DELETE_BEFORE:
//                break;
//            case DELETE_ALL:
//                break;
            default:
                throw new IllegalArgumentException("Invalid schedule type: " + type);
        }
    }
     */
}
