package knu.kproject.exception;

import knu.kproject.exception.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserExceptionHandler extends RuntimeException {
    private final ErrorCode errorCode;

    @Builder
    public UserExceptionHandler(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    @Builder
    public UserExceptionHandler(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}