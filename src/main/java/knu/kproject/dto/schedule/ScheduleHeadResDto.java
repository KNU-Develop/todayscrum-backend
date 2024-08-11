package knu.kproject.dto.schedule;


import knu.kproject.entity.schedule.Schedule;
import knu.kproject.global.ScheduleVisible;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ScheduleHeadResDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ScheduleVisible visible;
    private Long projectId;

    public ScheduleHeadResDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
        this.visible = schedule.getVisible();
        this.projectId = schedule.getProjectId();
    }
}
