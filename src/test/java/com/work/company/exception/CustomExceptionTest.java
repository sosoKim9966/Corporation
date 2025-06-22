package com.work.company.exception;
import com.work.company.common.exception.CustomException;
import com.work.company.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomExceptionTest {

    @Test
    void create_test() {
        // given
        String message = "테스트 예외";

        // when
        CustomException ex = new CustomException(ErrorCode.ADDRESS_NOT_FOUND, message);

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADDRESS_NOT_FOUND);
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void create_withCause_test() {
        // given
        Throwable cause = new RuntimeException("원인");
        String message = "예외 발생";

        // when
        CustomException ex = new CustomException(ErrorCode.JSON_PARSE_ERROR, message, cause);

        // then
        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getCause()).isEqualTo(cause);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.JSON_PARSE_ERROR);
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
