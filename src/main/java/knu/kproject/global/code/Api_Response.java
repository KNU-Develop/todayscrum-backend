package knu.kproject.global.code;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Api_Response<T> {

    // API 응답 결과 Response
    private T result;

    // API 응답 코드 Response
    private int code;

    // API 응답 코드 Message
    private String Description;

    @Builder
    public Api_Response(final T result, final int code, final String Description) {
        this.result = result;
        this.code = code;
        this.Description = Description;
    }
}