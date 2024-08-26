package knu.kproject.service;

import knu.kproject.dto.schedule.TimeSlot;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingSchedulerService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserRepository userRepository;

    public List<TimeSlot> recommendMeetingTimes(Long userId, UUID projectId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptionHandler(UserErrorCode.NOT_FOUND_USER));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.NOT_FOUND_PROJECT));
        ProjectUser projectUser = projectUserRepository.findByUserAndProject(user, project);
        Access.accessPossible(projectUser, ROLE.GUEST);

        List<UserSchedule> allSchedules = userScheduleRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);

        List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectId);

        List<TimeSlot> availableTimeSlots = calculateAvailableTimeSlots(allSchedules, projectUsers, startDate, endDate);

        return recommendOptimalTimeSlots(availableTimeSlots);
    }

    private List<TimeSlot> calculateAvailableTimeSlots(List<UserSchedule> allSchedules, List<ProjectUser> projectUsers, LocalDateTime startDate, LocalDateTime endDate) {
        // Step 1: 모든 일정의 시작 시간과 종료 시간을 수집하고, 시작 시간 기준으로 정렬
        List<Schedule> schedules = allSchedules.stream()
                .map(UserSchedule::getSchedule)
                .sorted(Comparator.comparing(Schedule::getStartDate))
                .collect(Collectors.toList());

        List<TimeSlot> availableSlots = new ArrayList<>();

        // Step 2: 첫 번째 빈 시간대 (시작 시간부터 첫 번째 일정 전까지)
        if (!schedules.isEmpty() && schedules.get(0).getStartDate().isAfter(startDate)) {
            availableSlots.add(new TimeSlot(startDate, schedules.get(0).getStartDate()));
        }

        // Step 3: 중간의 빈 시간대 (일정 사이의 시간대)
        for (int i = 0; i < schedules.size() - 1; i++) {
            LocalDateTime endCurrent = schedules.get(i).getEndDate();
            LocalDateTime startNext = schedules.get(i + 1).getStartDate();

            if (endCurrent.isBefore(startNext)) {
                availableSlots.add(new TimeSlot(endCurrent, startNext));
            }
        }

        // Step 4: 마지막 빈 시간대 (마지막 일정 이후)
        if (!schedules.isEmpty() && schedules.get(schedules.size() - 1).getEndDate().isBefore(endDate)) {
            availableSlots.add(new TimeSlot(schedules.get(schedules.size() - 1).getEndDate(), endDate));
        }

        // 전체 기간이 비어있는 경우
        if (schedules.isEmpty()) {
            availableSlots.add(new TimeSlot(startDate, endDate));
        }

        // Step 5: 각 시간대에 대해 가능한 참석자 수 계산
        for (TimeSlot slot : availableSlots) {
            int attendeeCount = 0;
            for (ProjectUser pu : projectUsers) {
                boolean isAvailable = true;
                for (UserSchedule us : allSchedules) {
                    if (us.getUser().equals(pu.getUser())) {
                        Schedule schedule = us.getSchedule();
                        if (!(schedule.getEndDate().isBefore(slot.getStartTime()) || schedule.getStartDate().isAfter(slot.getEndTime()))) {
                            isAvailable = false;
                            break;
                        }
                    }
                }
                if (isAvailable) {
                    attendeeCount++;
                }
            }
            slot.setAttendeeCount(attendeeCount);
        }

        return availableSlots;
    }

    private List<TimeSlot> recommendOptimalTimeSlots(List<TimeSlot> availableTimeSlots) {
        return availableTimeSlots.stream()
                // 01시부터 07시까지의 시간대를 제외
                .filter(slot -> slot.getStartTime().getHour() >= 7 || slot.getEndTime().getHour() <= 1)
                // 시간대가 30분 이상인지 확인
                .filter(slot -> java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes() >= 30)
                // 참석자 수가 많은 순서로 정렬하고, 시간이 같은 경우에는 시간이 긴 순서로 정렬
                .sorted((slot1, slot2) -> {
                    int attendeeComparison = Integer.compare(slot2.getAttendeeCount(), slot1.getAttendeeCount());
                    if (attendeeComparison != 0) {
                        return attendeeComparison; // 참석자 수가 많은 순서
                    }
                    long duration1 = java.time.Duration.between(slot1.getStartTime(), slot1.getEndTime()).toMinutes();
                    long duration2 = java.time.Duration.between(slot2.getStartTime(), slot2.getEndTime()).toMinutes();
                    return Long.compare(duration2, duration1); // 가장 긴 시간대 순
                })
                .limit(5) // 최대 5개의 시간대만 반환
                .collect(Collectors.toList());
    }
}
