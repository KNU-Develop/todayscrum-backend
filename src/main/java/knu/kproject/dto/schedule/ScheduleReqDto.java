package knu.kproject.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import knu.kproject.global.schedule.ScheduleVisible;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "일정 등록 요청 데이터")
public class ScheduleReqDto {
    @Schema(description = "일정 제목", example = "일정 제목")
    private String title;

    @Schema(description = "일정 내용", example = "일정 내용")
    private String content;

    @Schema(description = "일정 시작 날짜와 시간", example = "2024-08-10T10:00:00")
    private LocalDateTime startDate;

    @Schema(description = "일정 종료 날짜와 시간", example = "2024-08-10T11:00:00")
    private LocalDateTime endDate;

    @Schema(description = "일정의 공개 범위", example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"})
    private ScheduleVisible visible;

    @Schema(description = "프로젝트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID projectId;

    @Schema(description = "초대할 사용자 ID 목록", example = "[1, 2, 3]")
    private List<Long> inviteList;
}
