package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.dto.schedule.ScheduleCreateResultDto;
import knu.kproject.dto.schedule.ScheduleHeadResDto;
import knu.kproject.dto.schedule.ScheduleReqDto;
import knu.kproject.entity.Schedule;
import knu.kproject.entity.User;
import knu.kproject.entity.UserSchedule;
import knu.kproject.global.ScheduleRole;
import knu.kproject.global.ScheduleType;
import knu.kproject.repository.ScheduleRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Schedule newSchedule = new Schedule(scheduleReqDto);
        newSchedule = scheduleRepository.save(newSchedule); // 404 발생 대비
        List<Long> notFoundUserIds = null;
        if (scheduleReqDto.getProjectId() == null) {
            System.out.println("INVITE USER : "+inviteUser(newSchedule, userId));

        } else {
            notFoundUserIds = inviteUsersToProjectSchedule(newSchedule, scheduleReqDto.getInviteList());
        }
        return new ScheduleCreateResultDto(newSchedule.getId(), notFoundUserIds);
    }


    public boolean inviteUser(Schedule schedule, Long userId) {
        Optional<User> findUserOpt = userRepository.findById(userId);
        if (findUserOpt.isPresent()) {
            User user = findUserOpt.get();
            UserSchedule userSchedule = new UserSchedule(user, schedule);
            schedule.addUserSchedule(userSchedule);
            user.addUserSchedule(userSchedule);
            userScheduleRepository.save(userSchedule);

            return true;
        }
        return false;
    }

    public List<Long> inviteUsersToProjectSchedule(Schedule schedule, List<Long> inviteList) {
        List<Long> notFoundUserList = new ArrayList<>();
        for (Long userId : inviteList) {
            if (!inviteUser(schedule, userId))
                notFoundUserList.add(userId);
        }
        return notFoundUserList;
    }


    public List<ScheduleHeadResDto> getScheduleList(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
//        List<Schedule> schedules = scheduleRepository.findScheduleList(userId, startDate, endDate);
//        List<ScheduleHeadResDto> res = schedules.stream().map(ScheduleHeadResDto::new).collect(Collectors.toList());
//        return res;
        return null;
    }

    public Schedule getScheduleDetail(Long userId, Long scheduleId) {
        List<UserSchedule> findSchedule = userScheduleRepository.findByUserId(userId);

        for (UserSchedule userSchedule : findSchedule) {
            if (userSchedule.getUser().getId().equals(userId)) {
                Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
                if (schedule.isPresent()) {
                    return schedule.get();
                }
            }
        }
        return null;
    }
/*
    public void deleteScheduleById(Long scheduleId, ScheduleType type) {
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
