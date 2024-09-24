package knu.kproject.service;

import knu.kproject.dto.schedule.TimeSlot;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.MeetingErrorCode;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.exception.code.UserErrorCode;
import knu.kproject.global.ROLE;
import knu.kproject.global.functions.Access;
import knu.kproject.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

        if (allSchedules == null) {
            allSchedules = new ArrayList<>();
        }

        List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectId);

        List<TimeSlot> availableTimeSlots = calculateAvailableTimeSlots(allSchedules, projectUsers, startDate, endDate);

        List<TimeSlot> optimalTimeSlots = recommendOptimalTimeSlots(availableTimeSlots);

        boolean hasAvailableSlotForTwoOrMore = optimalTimeSlots.stream()
                .anyMatch(slot -> slot.getAttendeeCount() >= 2);

        if (!hasAvailableSlotForTwoOrMore) {
            return Collections.emptyList();
        }
        return recommendOptimalTimeSlots(availableTimeSlots);
    }

    public List<TimeSlot> recommendMeetingsForNextThreeDays(Long userId, UUID projectId) {
        LocalDate today = LocalDate.now();
        List<TimeSlot> allRecommendedSlots = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            LocalDateTime startOfDay = today.plusDays(i).atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(i).atTime(23, 59, 59);

            List<TimeSlot> dailyRecommendedSlots = recommendMeetingTimes(userId, projectId, startOfDay, endOfDay);

            allRecommendedSlots.addAll(dailyRecommendedSlots.stream().limit(2).collect(Collectors.toList()));
        }

        return allRecommendedSlots;
    }

    private List<TimeSlot> calculateAvailableTimeSlots(List<UserSchedule> allSchedules, List<ProjectUser> projectUsers, LocalDateTime startDate, LocalDateTime endDate) {
        List<Schedule> schedules = allSchedules.stream()
                .map(UserSchedule::getSchedule)
                .sorted(Comparator.comparing(Schedule::getStartDate))
                .toList();

        List<TimeSlot> availableSlots = new ArrayList<>();

        if (schedules.isEmpty()) {
            LocalDateTime startOfDay = startDate.withHour(7).withMinute(0);
            LocalDateTime endOfDay = startDate.withHour(23).withMinute(0);
            availableSlots.add(new TimeSlot(startOfDay, endOfDay));
        } else {
            if (schedules.get(0).getStartDate().isAfter(startDate)) {
                availableSlots.add(new TimeSlot(startDate, schedules.get(0).getStartDate()));
            }
            for (int i = 0; i < schedules.size() - 1; i++) {
                LocalDateTime endCurrent = schedules.get(i).getEndDate();
                LocalDateTime startNext = schedules.get(i + 1).getStartDate();

                if (endCurrent.isBefore(startNext)) {
                    availableSlots.add(new TimeSlot(endCurrent, startNext));
                }
            }
            if (schedules.get(schedules.size() - 1).getEndDate().isBefore(endDate)) {
                availableSlots.add(new TimeSlot(schedules.get(schedules.size() - 1).getEndDate(), endDate));
            }
        }

        // 프로젝트 사용자 별로 해당 시간대에 참석 가능한지 확인
        Map<User, List<Schedule>> userSchedulesMap = allSchedules.stream()
                .collect(Collectors.groupingBy(UserSchedule::getUser,
                        Collectors.mapping(UserSchedule::getSchedule, Collectors.toList())));

        for (TimeSlot slot : availableSlots) {
            int attendeeCount = 0;
            for (ProjectUser pu : projectUsers) {
                List<Schedule> userSchedules = userSchedulesMap.getOrDefault(pu.getUser(), new ArrayList<>());
                boolean isAvailable = userSchedules.stream()
                        .noneMatch(schedule ->
                                schedule.getEndDate().isAfter(slot.getStartTime()) && schedule.getStartDate().isBefore(slot.getEndTime()));
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
                .filter(slot -> slot.getStartTime().getHour() >= 7 && slot.getEndTime().getHour() <= 23) // 7시부터 23시까지
                .filter(slot -> java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes() >= 30)
                .sorted((slot1, slot2) -> {
                    int attendeeComparison = Integer.compare(slot2.getAttendeeCount(), slot1.getAttendeeCount());
                    if (attendeeComparison != 0) {
                        return attendeeComparison;
                    }
                    long duration1 = java.time.Duration.between(slot1.getStartTime(), slot1.getEndTime()).toMinutes();
                    long duration2 = java.time.Duration.between(slot2.getStartTime(), slot2.getEndTime()).toMinutes();
                    return Long.compare(duration2, duration1);
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}
