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
import knu.kproject.exception.code.CommonErrorCode;
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
                .orElseThrow(()-> new EntityNotFoundException(CommonErrorCode.NOT_FOUND.getMessage()));

        List<UserHeadDto> userDtos = null;

        if (findSchedule.getProjectId() != null) {
            userDtos = findSchedule.getUserSchedules().stream()
                    .map(userSchedule -> new UserHeadDto(userSchedule.getUser()))
                    .toList();
        }
        return new ScheduleDetailResDto(findSchedule, findSchedule.getProjectId(), userDtos);
    }


    public void updateSchedule(Long userId, Long scheduleId, ScheduleReqDto scheduleReqDto) {
        Schedule schedule = userScheduleRepository.findScheduleByUserIdAndScheduleId(userId, scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(CommonErrorCode.NOT_FOUND.getMessage()));


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
