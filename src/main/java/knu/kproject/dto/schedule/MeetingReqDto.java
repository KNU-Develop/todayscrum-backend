package knu.kproject.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MeetingReqDto {
    private UUID projectId;
    private LocalDate startDate;
    private LocalDate endDate;
}
