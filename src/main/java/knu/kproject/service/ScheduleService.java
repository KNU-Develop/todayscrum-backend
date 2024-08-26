package knu.kproject.service;

import knu.kproject.dto.schedule.*;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.exception.ScheduleException;
import knu.kproject.exception.code.ScheduleErrorCode;
import knu.kproject.global.schedule.ScheduleInviteState;
import knu.kproject.global.schedule.ScheduleUpdateType;
import knu.kproject.repository.ScheduleRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static knu.kproject.global.schedule.ScheduleInviteState.ACCEPT;
import static knu.kproject.global.schedule.ScheduleInviteState.WAIT;
import static knu.kproject.global.schedule.ScheduleRole.*;


@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResDto createSchedule(Long userId, ScheduleReqDto scheduleReqDto, Project project) {
        Schedule newSchedule = new Schedule(scheduleReqDto, project);
        if (scheduleReqDto.getProjectId() == null) {
            inviteUser(newSchedule, userId, ACCEPT);
        } else {
            for (Long inviteUserId : scheduleReqDto.getInviteList()) {
                inviteUser(newSchedule, inviteUserId, WAIT);
            }
        }
        scheduleRepository.save(newSchedule);
        return new ScheduleResDto(newSchedule);
    }

    // 일정 초대 로직 : UserSchedule 생성 및 관리
    private void inviteUser(Schedule schedule, Long userId, ScheduleInviteState state) {
        Optional<User> findUserOpt = userRepository.findById(userId);
        if (findUserOpt.isPresent()) {
            UserSchedule userSchedule = new UserSchedule(findUserOpt.get(), schedule, state);
            userScheduleRepository.save(userSchedule);
        }
    }

    /*
        - 일정 목록 조회
     */
    @Transactional
    public List<ScheduleResDto> getScheduleList(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Schedule> schedules = scheduleRepository.findScheduleList(userId, startDate, endDate)
                .orElse(Collections.emptyList());
        return schedules.stream().map(ScheduleResDto::new).collect(Collectors.toList());
    }

    /*
        - 일정 관련자에 한해(참여 상태 고려 안함) 일정 세부 정보 반환
        - 프로젝트 일정인 경우 참여자 정보(id, 이름, 이메일, 참여 상태)를 포함해 반환
     */
    @Transactional
    public ScheduleResDto getSchedule(Long userId, Long scheduleId) {
        UserSchedule findSchedule = userScheduleRepository.findUserScheduleByUser_IdAndSchedule_Id(userId, scheduleId)
                .orElseThrow(()-> new ScheduleException(ScheduleErrorCode.NOT_FOUND)); // 404
        Schedule schedule = findSchedule.getSchedule();
        return new ScheduleResDto(schedule);
    }

    // 여기까지 완료




    @Transactional
    public void updateSchedule(Long userId, Long scheduleId, ScheduleReqDto scheduleReqDto, Project project) {
        UserSchedule findUserSchedule = userScheduleRepository.findUserScheduleByUser_IdAndSchedule_Id(userId, scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.NO_INVITE_SCHEDULE)); // 403

        if (haveChangeableRole(findUserSchedule)) {
            Schedule schedule = findUserSchedule.getSchedule();
            schedule.updateSchedule(scheduleReqDto, project);
            List<Long> newInviteUserIds = scheduleReqDto.getInviteList();
            updateInviteList(findUserSchedule, newInviteUserIds);

        } else {
            throw new ScheduleException(ScheduleErrorCode.NO_ACCESS_SCHEDULE); // 403
        }
    }

    private void updateInviteList(UserSchedule userSchedule, List<Long> newInviteUserIds) {
        Schedule schedule = userSchedule.getSchedule();
        List<UserSchedule> prevUserSchedules = schedule.getUserSchedules();
        List<User> prevInviteUsers = prevUserSchedules.stream().map(UserSchedule::getUser).toList();
        List<Long> prevInviteUserIds = prevInviteUsers.stream().map(User::getId).toList();

        // 새로 일정에 초대되는 인원 new - prev
        List<Long> newInviteList = new ArrayList<>(newInviteUserIds);
        newInviteList.removeAll(prevInviteUserIds);
        for (Long inviteUserId : newInviteList) {
            inviteUser(schedule, inviteUserId, WAIT);
        }


        // 기존에 초대 유저 중 더 이상 초대되지 않는 인원 prev - new
        List<Long> removeInviteList = new ArrayList<>(prevInviteUserIds);
        removeInviteList.removeAll(newInviteUserIds);
        for (Long userId : removeInviteList) {
            for (User prevInviteUser : prevInviteUsers) {
                if (userId.equals(prevInviteUser.getId())) {
                    userSchedule.unUserSchedule();
                    userScheduleRepository.deleteByUserAndSchedule(prevInviteUser, schedule);
                }
            }
        }
    }

    // 일정 수정 권한 확인 로직
    private Boolean haveChangeableRole(UserSchedule userSchedule) {
        return switch (userSchedule.getRole()) {
            case OWNER, WRITE -> true;
            default -> false;
        };
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId, ScheduleUpdateType type) {
        UserSchedule findUserSchedule = userScheduleRepository.findUserScheduleByUser_IdAndSchedule_Id(userId, scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.NO_INVITE_SCHEDULE));
        if (haveChangeableRole(findUserSchedule)) {
            switch (type) {
                case THIS:
                    List<UserSchedule> userSchedules = findUserSchedule.getSchedule().getUserSchedules();
                    for (UserSchedule userSchedule : userSchedules) {
                        userSchedule.unUserSchedule();
                    }
                    userScheduleRepository.deleteAll(userSchedules);
                    scheduleRepository.delete(findUserSchedule.getSchedule());
            }
        } else {
            throw new ScheduleException(ScheduleErrorCode.NO_ACCESS_SCHEDULE);
        }
    }
}
