package knu.kproject.api;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private String code;
    private T result;

    @Builder
    public ApiResponse(String code, T result) {
        this.code = code;
        this.result = result;
    }
}
