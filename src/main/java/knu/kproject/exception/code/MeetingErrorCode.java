package knu.kproject.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MeetingErrorCode implements ErrorCode{
    NO_AVAILABLE_MEETING_TIME(HttpStatus.BAD_REQUEST, "회의를 위한 적절한 시간이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
