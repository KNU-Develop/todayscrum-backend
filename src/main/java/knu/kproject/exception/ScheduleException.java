package knu.kproject.exception;

import knu.kproject.exception.code.ErrorCode;

public class ScheduleException extends RuntimeException {
    public final ErrorCode errorCode;

    public ScheduleException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
