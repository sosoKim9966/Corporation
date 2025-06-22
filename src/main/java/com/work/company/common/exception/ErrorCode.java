package com.work.company.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    DOWNLOAD_FAIL("exception.error.download.fail", HttpStatus.INTERNAL_SERVER_ERROR),
    DOWNLOAD_IO_ERROR("exception.error.download.io", HttpStatus.INTERNAL_SERVER_ERROR),

    HTTP_STATUS_ERROR("exception.error.httpStatus", HttpStatus.BAD_GATEWAY),
    NON_JSON_RESPONSE("exception.error.nonJson", HttpStatus.BAD_GATEWAY),
    JSON_PARSE_ERROR("exception.error.jsonParse", HttpStatus.BAD_REQUEST),

    INVALID_ARGUMENT("exception.error.invalidArgument", HttpStatus.BAD_REQUEST),
    TRANSACTION_ROLLBACK("exception.error.transaction.rollback", HttpStatus.INTERNAL_SERVER_ERROR),

    COR_API_NO_RESPONSE("exception.error.corApi", HttpStatus.BAD_GATEWAY),
    ADR_API_NO_RESULT("exception.error.adrApi", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND("exception.error.address.notFound", HttpStatus.UNPROCESSABLE_ENTITY),
    ADMCD_NOT_FOUND("exception.error.admCd.notFound", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_INPUT("exception.error.invalidInput", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String message;
    private final HttpStatus status;
}
