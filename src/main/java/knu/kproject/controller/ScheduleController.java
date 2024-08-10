package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import knu.kproject.dto.schedule.ScheduleCreateResultDto;
import knu.kproject.dto.schedule.ScheduleDetailResDto;
import knu.kproject.dto.schedule.ScheduleReqDto;
import knu.kproject.dto.schedule.ScheduleHeadResDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.repository.UserRepository;
import knu.kproject.service.ScheduleService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(value = "/schedule",produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Tag(name = "스케쥴 API", description = "스케줄 관리를 위한 API 입니다.")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "일정 등록", description = "일정을 등록합니다. 프로젝트 ID가 존재할 경우 프로젝트 일정으로 등록하며 사용자를 자동으로 초대합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정이 성공적으로 등록 되었습니다."),
            @ApiResponse(responseCode = "206", description = "일정은 생성되었으나, 초대받지 못한 유저가 있습니다."),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 프로젝트 ID 또는 참여자 입니다."),
    })
    public ResponseEntity<Api_Response<ScheduleCreateResultDto>> createSchedule(
            @AuthenticationPrincipal Long userId,
            @RequestBody ScheduleReqDto scheduleDto) {
        ScheduleCreateResultDto scheduleCreateResultDto = scheduleService.createSchedule(userId, scheduleDto);
        if (scheduleCreateResultDto.getNotFoundUserIds() == null) {
            return ApiResponseUtil.createResponse(
                    HttpStatus.CREATED.value(),
                    "SUCCESS CREATE" + scheduleCreateResultDto.getScheduleId(),
                    scheduleCreateResultDto
            );
        } else {
            return ApiResponseUtil.createResponse(
                    HttpStatus.PARTIAL_CONTENT.value(),
                    "FAIL INVITE USER",
                    scheduleCreateResultDto
            );
        }
    }

    @GetMapping
    public ResponseEntity<Api_Response<List<ScheduleHeadResDto>>> getScheduleList(
            @AuthenticationPrincipal Long userId,
            @RequestParam("startDate")
            LocalDate startDate,
            @RequestParam("endDate")
            LocalDate endDate
            ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<ScheduleHeadResDto> scheduleList = scheduleService.getScheduleList(userId, startDateTime, endDateTime);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                scheduleList
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api_Response<ScheduleDetailResDto>> getScheduleDetail(
            @AuthenticationPrincipal
            Long userId,
            @RequestParam("id") @Schema(description = "조회 일정 ID")
            Long scheduleId) {
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                scheduleService.getScheduleDetail(userId, scheduleId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Api_Response<Null>> updateSchedule(
            @AuthenticationPrincipal Long userId,
            @RequestParam("id") @Schema(description = "수정 일정 ID")
            Long scheduleId,
            @RequestBody ScheduleReqDto updateReqDto) {

        scheduleService.updateSchedule(userId, scheduleId, updateReqDto);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
    }

    /*
    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "일정 ID와 삭제 유형(type)으로 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "반복 일정 삭제 유형(type)이 잘못 되었습니다."),
            @ApiResponse(responseCode = "404", description = "삭제 할 일정을 찾을 수 없습니다.")
    })
    public ResponseEntity<Api_Response<Void>> deleteSchedule(
            @Parameter(description = "삭제할 일정의 ID", required = true)
            @PathVariable Long scheduleId,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "반복 일정 삭제 유형",
                    schema = @Schema(implementation = ScheduleType.class))
            @RequestParam(value="type", defaultValue = "THIS") ScheduleType type) {

        ResponseEntity<Api_Response<Void>> result;
        try {
            scheduleService.deleteScheduleById(scheduleId, type);
            result = ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            result = ApiResponseUtil.createBadRequestResponse(e.getMessage());
        } catch (EntityNotFoundException e) {
            result = ApiResponseUtil.createNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            result = ApiResponseUtil.createErrorResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
            );
        }
        return result;
    }

     */
}
