package com.work.company.common.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleCustom(CustomException ex) {
        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle(ex.getMessage());
        return ResponseEntity.of(pd).build();
    }
}
