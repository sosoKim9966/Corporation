package com.work.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private final ErrorCode  errorCode;
    private final HttpStatus status;
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status    = errorCode.getStatus();
    }

    public CustomException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status    = errorCode.getStatus();
    }

    public ErrorCode  getErrorCode() { return errorCode; }
    public HttpStatus getStatus()    { return status;    }
}
