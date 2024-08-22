package knu.kproject.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCode implements ErrorCode {
    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ""),

    // 401
    BAD_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "Authorization 오류"),

    // 403
    FORBIDEN_ROLE(HttpStatus.FORBIDDEN, "접근 권한이 부족합니다."),
    INVITED_YET(HttpStatus.FORBIDDEN, "초대를 수락하지 않았습니다."),
    NOT_INVITE(HttpStatus.FORBIDDEN, "초대받지 못했습니다."),

    // 404
    NOT_FOUND_PROJECT(HttpStatus.NOT_FOUND, "프로젝트가 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저가 없습니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "보드가 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글이 없습니다."),
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "알림이 없습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
