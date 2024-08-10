package knu.kproject.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    // 400
    INVALID_REQUEST_USER_ID(HttpStatus.BAD_REQUEST, ""),

    // 404
    NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
