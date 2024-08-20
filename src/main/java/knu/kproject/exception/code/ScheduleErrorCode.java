package knu.kproject.exception.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    // 400
    INVALID_REQUEST_USER_ID(HttpStatus.BAD_REQUEST, ""),

    // 403
    NO_INVITE_SCHEDULE(HttpStatus.FORBIDDEN, "No invite schedule, need invite"),
    NO_ACCESS_SCHEDULE(HttpStatus.FORBIDDEN, "No access schedule, need role 'OWNER' or 'WRITE'"),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found Schedule"),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
