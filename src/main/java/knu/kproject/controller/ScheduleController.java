package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import knu.kproject.dto.schedule.ScheduleReqDto;
import knu.kproject.dto.schedule.ScheduleResDto;
import knu.kproject.entity.project.Project;
import knu.kproject.exception.UserExceptionHandler;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.global.schedule.ScheduleUpdateType;
import knu.kproject.repository.ProjectRepository;
import knu.kproject.repository.UserRepository;
import knu.kproject.service.ScheduleService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
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
    private final ProjectRepository projectRepository;

    @PostMapping
    @Operation(summary = "일정 등록", description = "일정을 등록합니다. 프로젝트 ID가 존재할 경우 프로젝트 일정으로 등록하며 사용자를 자동으로 초대합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 등록 성공"),
    })
    public ResponseEntity<Api_Response<ScheduleResDto>> createSchedule(
            @AuthenticationPrincipal Long userId,
            @RequestBody ScheduleReqDto scheduleDto) {
        Project project = projectRepository.findById(scheduleDto.getProjectId())
                .orElseThrow(() -> new UserExceptionHandler(ProjectErrorCode.NOT_FOUND_PROJECT));
        ScheduleResDto scheduleResDto = scheduleService.createSchedule(userId, scheduleDto, project);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.INSERT_SUCCESS.getMessage(),
                scheduleResDto
            );
    }

    @GetMapping("/list")
    @Operation(summary = "일정 기간 조회", description = "기간 별 일정을 조회합니다. 조회된 일정이 없을 경우 빈 List를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 기간 조회 성공"),

    })
    public ResponseEntity<Api_Response<List<ScheduleResDto>>> getScheduleList(
            @AuthenticationPrincipal Long userId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<ScheduleResDto> scheduleList = scheduleService.getScheduleList(userId, startDateTime, endDateTime);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                scheduleList
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "일정 조회", description = "일정 ID로 하나의 일정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 일정.")
    })
    public ResponseEntity<Api_Response<ScheduleResDto>> getSchedule(
            @AuthenticationPrincipal Long userId,
            @PathVariable("id") @Schema(description = "조회 일정 ID") Long scheduleId
    ) {
        ScheduleResDto scheduleResDto = scheduleService.getSchedule(userId, scheduleId);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                scheduleResDto
        );
    }


    @PutMapping("/{id}")
    @Operation(summary = "일정 수정", description = "일정 ID와 수정한 일정 정보로 일정을 업데이트 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            @ApiResponse(responseCode = "403", description = "초대되지 않은 일정 / 수정 권한 없음")
    })
    public ResponseEntity<Api_Response<Void>> updateSchedule(
            @AuthenticationPrincipal Long userId,
            @PathVariable("id") @Schema(description = "일정 ID") Long scheduleId,
            @RequestBody ScheduleReqDto updateReqDto
    ) {
        Project project = projectRepository.findById(updateReqDto.getProjectId())
                .orElseThrow(() -> new UserExceptionHandler(ProjectErrorCode.NOT_FOUND_PROJECT));
        scheduleService.updateSchedule(userId, scheduleId, updateReqDto, project);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS.getMessage());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "일정 삭제", description = "일정 ID와 삭제 유형(type)으로 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "초대되지 않은 일정 / 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "삭제 할 일정을 찾지 못함")
    })
    public ResponseEntity<Api_Response<Void>> deleteSchedule(
            @AuthenticationPrincipal Long userId,
            @PathVariable("id") @Schema(description = "일정 ID") Long scheduleId,
            @RequestParam(value="type", defaultValue = "THIS")
            @Schema(description = "삭제할 일정의 범위를 지정합니다. 가능한 값: THIS",
                    type = "string",
                    allowableValues = {"THIS"},
                    example = "THIS") ScheduleUpdateType type
    ) {
        scheduleService.deleteSchedule(userId, scheduleId, type);
        return ApiResponseUtil.createSuccessResponse(SuccessCode.DELETE_SUCCESS.getMessage());
    }
}
