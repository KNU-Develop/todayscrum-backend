package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.schedule.MeetingReqDto;
import knu.kproject.dto.schedule.TimeSlot;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.MeetingSchedulerService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "Meeting Scheduler API", description = "회의 추천 API")
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class MeetingSchedulerController {

    private final MeetingSchedulerService meetingSchedulerService;

    @Operation(summary = "회의 시간 추천", description = "프로젝트와 시간 범위에 따른 회의 시간을 추천하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천된 회의 시간 목록"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "Authorization 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("")
    public ResponseEntity<Api_Response<List<TimeSlot>>> recommendMeetingTimes(
            @AuthenticationPrincipal Long userId,
            @RequestBody MeetingReqDto meetingReqDto) {

        LocalDateTime startDateTime = meetingReqDto.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = meetingReqDto.getEndDate().atTime(23, 59, 59);

        List<TimeSlot> timeSlots = meetingSchedulerService.recommendMeetingTimes(userId, meetingReqDto.getProjectId(), startDateTime, endDateTime);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getMessage(), timeSlots);
    }

    @Operation(summary = "다음 3일간 회의 시간 추천", description = "오늘 날짜부터 3일 동안 하루에 2개의 회의 시간을 추천하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천된 회의 시간 목록"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "Authorization 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/three-days")
    public ResponseEntity<Api_Response<List<TimeSlot>>> recommendMeetingsForNextFourDays(
            @AuthenticationPrincipal Long userId,
            @RequestBody MeetingReqDto meetingReqDto) {

        List<TimeSlot> timeSlots = meetingSchedulerService.recommendMeetingsForNextThreeDays(userId, meetingReqDto.getProjectId());
        return ApiResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS.getMessage(), timeSlots);
    }
}
