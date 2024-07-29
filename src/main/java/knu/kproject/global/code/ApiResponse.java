package knu.kproject.global.code;

import lombok.Builder;
import lombok.Getter;


@Getter
public class ApiResponse<T> {

    // API 응답 결과 Response
    private T result;

    // API 응답 코드 Response
    private int code;

    // API 응답 코드 Message
    private String msg;

    @Builder
    public ApiResponse(final T result, final int code, final String msg) {
        this.result = result;
        this.code = code;
        this.msg = msg;
    }
}