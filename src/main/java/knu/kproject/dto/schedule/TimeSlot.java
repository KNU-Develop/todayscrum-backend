package knu.kproject.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int attendeeCount;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendeeCount = 0;
    }
}

