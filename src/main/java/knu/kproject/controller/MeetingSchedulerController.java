package knu.kproject.controller;

import knu.kproject.dto.schedule.TimeSlot;
import knu.kproject.service.MeetingSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meeting-scheduler")
@RequiredArgsConstructor
public class MeetingSchedulerController {

    private final MeetingSchedulerService meetingSchedulerService;

    @GetMapping("/recommend")
    public List<TimeSlot> recommendMeetingTimes(
            @AuthenticationPrincipal Long userId,
            @RequestParam UUID projectId,
            @RequestParam String startDate,  // ISO 8601 형식의 날짜 문자열로 받을 수 있음
            @RequestParam String endDate) {

        // 문자열로 받은 날짜를 LocalDateTime으로 변환
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        // 서비스 호출하여 추천 시간대 목록 반환
        return meetingSchedulerService.recommendMeetingTimes(userId, projectId, start, end);
    }
}
