package knu.kproject.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jdk.jfr.Experimental;
import knu.kproject.exception.code.CommonErrorCode;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.ErrorCode;
import knu.kproject.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Api_Response<Object>> handle(DataIntegrityViolationException e) {
        String message = "필수 필드에 null이 들어갔습니다: ";

        String exceptionMessage = e.getMostSpecificCause().getMessage();

        if (exceptionMessage != null) {
            // 정규 표현식을 이용해 "Column '필드명' cannot be null" 패턴에서 필드명 추출
            Pattern pattern = Pattern.compile("Column '(.*?)' cannot be null");
            Matcher matcher = pattern.matcher(exceptionMessage);

            if (matcher.find()) {
                String columnName = matcher.group(1);
                message += columnName;
            } else {
                message += "알 수 없는 필드";
            }
        }
        return ApiResponseUtil.createBadRequestResponse(message);
    }

    @ExceptionHandler(ProjectException.class)
    protected ResponseEntity<Api_Response<Object>> projectHandler(ProjectException e) {
        return ApiResponseUtil.createErrorResponse(e.errorCode);
    }

    @ExceptionHandler(ScheduleException.class)
    public final ResponseEntity<?> handleScheduleException(ScheduleException e) {
        return ApiResponseUtil.createErrorResponse(e.errorCode);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Api_Response<Object>> handle(RuntimeException e) {
        return ApiResponseUtil.createErrorResponse(
                e.getMessage(),
                ErrorCode.INSERT_ERROR.getStatus()
        );
    }
}
