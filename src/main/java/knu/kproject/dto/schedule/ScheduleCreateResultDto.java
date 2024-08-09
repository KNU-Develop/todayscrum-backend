package knu.kproject.dto.schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ScheduleCreateResultDto {
    private final Long scheduleId;
    private final List<Long> notFoundUserIds;

    public ScheduleCreateResultDto(Long scheduleId, List<Long> notFoundUserIds) {
        this.scheduleId = scheduleId;
        this.notFoundUserIds = notFoundUserIds;
    }
}
