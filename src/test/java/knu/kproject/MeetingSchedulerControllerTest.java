package knu.kproject;

import knu.kproject.dto.schedule.TimeSlot;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.global.CHOICE;
import knu.kproject.global.ROLE;
import knu.kproject.repository.*;
import knu.kproject.service.MeetingSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MeetingSchedulerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserScheduleRepository userScheduleRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @InjectMocks
    private MeetingSchedulerService meetingSchedulerService;

    private User user1, user2, user3, user4, user5;
    private Project project;
    private List<UserSchedule> userSchedules;
    private List<ProjectUser> projectUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 사용자 생성
        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);
        user3 = new User();
        user3.setId(3L);
        user4 = new User();
        user4.setId(4L);
        user5 = new User();
        user5.setId(5L);

        // 프로젝트 생성
        project = new Project();
        project.setId(UUID.randomUUID());

        // ProjectUser 생성 및 Mock 설정
        projectUsers = new ArrayList<>();
        projectUsers.add(new ProjectUser(user1, project, ROLE.MASTER, CHOICE.수락));
        projectUsers.add(new ProjectUser(user2, project, ROLE.GUEST, CHOICE.수락));
        projectUsers.add(new ProjectUser(user3, project, ROLE.GUEST, CHOICE.수락));
        projectUsers.add(new ProjectUser(user4, project, ROLE.GUEST, CHOICE.수락));
        projectUsers.add(new ProjectUser(user5, project, ROLE.GUEST, CHOICE.수락));

        when(projectUserRepository.findByProjectId(any(UUID.class))).thenReturn(projectUsers);

        // 사용자 일정 설정
        userSchedules = new ArrayList<>();
        userSchedules.add(createUserSchedule(user1, LocalDateTime.of(2024, 8, 28, 8, 0), LocalDateTime.of(2024, 8, 28, 9, 0)));
        userSchedules.add(createUserSchedule(user2, LocalDateTime.of(2024, 8, 28, 10, 0), LocalDateTime.of(2024, 8, 28, 11, 0)));
        userSchedules.add(createUserSchedule(user3, LocalDateTime.of(2024, 8, 28, 9, 0), LocalDateTime.of(2024, 8, 28, 10, 0)));
        userSchedules.add(createUserSchedule(user4, LocalDateTime.of(2024, 8, 28, 11, 0), LocalDateTime.of(2024, 8, 28, 12, 0)));
        userSchedules.add(createUserSchedule(user5, LocalDateTime.of(2024, 8, 28, 17, 0), LocalDateTime.of(2024, 8, 28, 18, 0)));

        when(userScheduleRepository.findByProjectIdAndDateRange(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(userSchedules);

        // 사용자 및 프로젝트 Mock 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(projectRepository.findById(any(UUID.class))).thenReturn(Optional.of(project));
    }

    @Test
    void recommendMeetingTimes_ShouldReturnOptimalTimeSlots() {
        // 테스트 실행
        List<TimeSlot> timeSlots = meetingSchedulerService.recommendMeetingTimes(
                1L, project.getId(), LocalDateTime.of(2024, 8, 28, 8, 0), LocalDateTime.of(2024, 8, 28, 19, 0));

        // 검증
        assertNotNull(timeSlots);
        assertFalse(timeSlots.isEmpty());
        assertTrue(timeSlots.size() <= 5);

        for (TimeSlot slot : timeSlots) {
            System.out.println("추천된 회의 시간: " + slot.getStartTime() + " ~ " + slot.getEndTime() +
                    ", 참석자 수: " + slot.getAttendeeCount());
            assertTrue(slot.getAttendeeCount() > 0, "Attendee count should be greater than 0");
            assertTrue(java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes() >= 30);
        }
    }

    // Helper method to create UserSchedule
    private UserSchedule createUserSchedule(User user, LocalDateTime start, LocalDateTime end) {
        Schedule schedule = new Schedule();
        schedule.setStartDate(start);
        schedule.setEndDate(end);

        UserSchedule userSchedule = new UserSchedule();
        userSchedule.setUser(user);
        userSchedule.setSchedule(schedule);

        return userSchedule;
    }
}
