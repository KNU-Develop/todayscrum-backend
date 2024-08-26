package knu.kproject.dto.schedule;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Embeddable
public class DateRange {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
