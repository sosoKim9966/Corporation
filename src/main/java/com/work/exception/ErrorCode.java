package com.work.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    DOWNLOAD_FAIL("exception.error.download.fail", HttpStatus.BAD_GATEWAY),
    DOWNLOAD_IO_ERROR("exception.error.download.io", HttpStatus.BAD_GATEWAY),

    HTTP_STATUS_ERROR("exception.error.httpStatus", HttpStatus.BAD_GATEWAY),
    NON_JSON_RESPONSE("exception.error.nonJson", HttpStatus.BAD_GATEWAY),
    JSON_PARSE_ERROR("exception.error.jsonParse", HttpStatus.BAD_GATEWAY),

    INVALID_ARGUMENT("exception.error.invalidArgument", HttpStatus.BAD_REQUEST),
    TRANSACTION_ROLLBACK("exception.error.transaction.rollback", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus status;
}
