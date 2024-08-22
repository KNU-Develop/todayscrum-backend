package knu.kproject.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.global.schedule.ScheduleInviteState;
import knu.kproject.global.schedule.ScheduleVisible;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduleResDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Schema(description = "일정의 공개 범위", example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"})
    private ScheduleVisible visible;
    private Long projectId;
    private List<ScheduleInviteUserDto> inviteList = new ArrayList<>();

    public ScheduleResDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.visible = schedule.getVisible();
        this.projectId = schedule.getProjectId();
        List<UserSchedule> userScheduleList = schedule.getUserSchedules();
        for (UserSchedule userSchedule : userScheduleList) {
            this.inviteList.add(new ScheduleInviteUserDto(userSchedule.getUser(), userSchedule.getInviteState()));
        }
    }
}

@Getter
class ScheduleInviteUserDto {
    private Long id;
    private String name;
    private String email;
    @Schema(description = "초대 상태", example = "ACCEPT", allowableValues = {"ACCEPT", "REJECT", "WAIT"})
    private ScheduleInviteState state;

    public ScheduleInviteUserDto(User user, ScheduleInviteState state) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.state = state;
    }
}
