package me.lolicom.blog.web.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.time.Instant;

/**
 * @author lolicom
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ApiResult {
    public static final ApiResult OK = ok(null);

    private Instant timestamp = Instant.now();
    private int code;
    private String status;
    private String message;
    private Object data;

    private ApiResult(int code, String status, String message, Object data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ApiResult ok(Object data) {
        return builder().withStatus(HttpStatus.OK)
                .withData(data)
                .build();
    }

    public static ApiResult ok(@NonNull String message, Object data) {
        return builder().withStatus(HttpStatus.OK)
                .withMessage(message)
                .withData(data)
                .build();
    }

    public static ApiResult status(@NonNull HttpStatus status) {
        return builder().withStatus(status).build();
    }

    public static ApiResult status(@NonNull HttpStatus status, String message) {
        return builder().withStatus(status)
                .withMessage(message)
                .build();
    }

    public static ApiResult status(@NonNull HttpStatus httpStatus, @NonNull String message, Object data) {
        return builder().withStatus(httpStatus)
                .withMessage(message)
                .withData(data)
                .build();
    }

    public static ApiResultBuilder builder() {
        return new ApiResultBuilder();
    }

    /**
     * Default status-code is 200[OK], http-status reason phrase used as
     * message if not set.
     */
    private static class ApiResultBuilder {
        private Integer code;
        private String status;
        private String message;
        private Object data;

        public ApiResultBuilder withStatus(@NonNull HttpStatus httpStatus) {
            if (this.code == null) {
                this.code = httpStatus.value();
            }
            this.code = httpStatus.value();
            if (this.message != null) {
                this.message = httpStatus.getReasonPhrase();
            }
            if (httpStatus.isError()) {
                this.status = "error";
            } else {
                this.status = "success";
            }
            return this;
        }

        public ApiResultBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ApiResultBuilder withData(Object data) {
            this.data = data;
            return this;
        }

        public ApiResult build() {
            return new ApiResult(code, status, message, data);
        }
    }
}