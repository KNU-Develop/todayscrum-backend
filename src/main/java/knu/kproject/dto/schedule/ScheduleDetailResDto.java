package knu.kproject.dto.schedule;

import knu.kproject.dto.UserDto.UserHeadDto;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.global.ScheduleVisible;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleDetailResDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ScheduleVisible visible;
    private Long projectId;
    private List<UserHeadDto> inviteList;

    public ScheduleDetailResDto(Schedule schedule, Long projectId, List<UserHeadDto> inviteList) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.visible = schedule.getVisible();
        this.projectId = projectId;
        this.inviteList = inviteList;
    }
}
