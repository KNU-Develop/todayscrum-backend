package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.global.code.ScheduleType;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.ScheduleService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "스케쥴 API", description = "스케줄 관리를 위한 API 입니다.")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "일정 ID와 삭제 유형(type)으로 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "반복 일정 삭제 유형(type)이 잘못 되었습니다."),
            @ApiResponse(responseCode = "404", description = "삭제 할 일정을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
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
}
