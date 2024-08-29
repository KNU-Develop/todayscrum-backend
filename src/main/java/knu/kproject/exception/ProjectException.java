package knu.kproject.exception;


import knu.kproject.exception.code.ErrorCode;

public class ProjectException extends RuntimeException {
    public final ErrorCode errorCode;

    public ProjectException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ProjectException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
