package carear.document.be.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ApiResponseDto<T> {
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String ERROR = "error";

    private String status;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<T>(SUCCESS, null, data);
    }

    public static ApiResponseDto<?> fail(String message) {
        return new ApiResponseDto<>(FAIL, message, null);
    }

    public static ApiResponseDto<?> error(String message) {
        return new ApiResponseDto<>(ERROR, message,null);
    }
}
