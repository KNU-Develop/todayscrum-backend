package knu.kproject.dto.schedule;

import knu.kproject.global.ScheduleVisible;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleReqDto {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ScheduleVisible visible;
    private Long projectId;
    private List<Long> inviteList;
}