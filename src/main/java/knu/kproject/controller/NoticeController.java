package knu.kproject.controller;

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

@Tag(name = "Notice API", description = "알람 API 명세서 입니다.")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/")
    public ResponseEntity<Api_Response<Object>> getNotice(@AuthenticationPrincipal Long token) {
        List<OutNoticeDto> noticeDtoList = noticeService.getNotice(token);

        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                noticeDtoList
        );
    }

    @PostMapping("/")
    public ResponseEntity<Api_Response<Object>> acceptInvite(@AuthenticationPrincipal Long token, @RequestBody InNoticeDto input) {
        System.out.println(input);

        noticeService.acceptInvite(token, input);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }
}
