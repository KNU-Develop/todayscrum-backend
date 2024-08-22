package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.notice.InNoticeDto;
import knu.kproject.dto.notice.OutNoticeDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.NoticeService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notice API", description = "알림 API 명세서 입니다.")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "알림 조회", description = "알림 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/")
    public ResponseEntity<Api_Response<Object>> getNotice(@AuthenticationPrincipal Long token) {
        List<OutNoticeDto> noticeDtoList = noticeService.getNotice(token);

        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                noticeDtoList
        );
    }

    @Operation(summary = "알림 여부", description = "알림 선택 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 선택 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "404", description = "알림이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/")
    public ResponseEntity<Api_Response<Object>> acceptInvite(@AuthenticationPrincipal Long token, @RequestBody InNoticeDto input) {
        noticeService.acceptInvite(token, input);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }
}
