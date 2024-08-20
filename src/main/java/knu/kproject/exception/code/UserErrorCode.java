package knu.kproject.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "not found user with id.")
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
