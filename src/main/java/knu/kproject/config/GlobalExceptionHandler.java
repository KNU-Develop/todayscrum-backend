package knu.kproject.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.executable.ValidateOnExecution;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 403 권한 오류
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Api_Response<Object>> handle(NullPointerException e) {
        return ApiResponseUtil.createErrorResponse(
                ErrorCode.FORBIDDEN_ERROR.getMessage(),
                ErrorCode.FORBIDDEN_ERROR.getStatus()
        );
    }
    // 401 헤더 오류
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Api_Response<Object>> handle(IllegalArgumentException e) {
        return ApiResponseUtil.createUnAuthorization();
    }
    // 404 데이터 없음
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Api_Response<Object>> handle(EntityNotFoundException e) {
        return ApiResponseUtil.createNotFoundResponse(
                ErrorCode.NOT_FOUND_ERROR.getMessage()
        );
    }
    // 500 서버 오류
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Api_Response<Object>> handle(RuntimeException e) {

        return ApiResponseUtil.createErrorResponse(
                e.getMessage(),
                ErrorCode.SELECT_ERROR.getStatus()
        );
    }
}
