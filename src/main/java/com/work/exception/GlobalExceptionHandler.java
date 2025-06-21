package com.work.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> GrahandleCustom(CustomException ex) {
        ProblemDetail pd = ProblemDetail
                .forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle(ex.getCode());           // 추가 필드
        return ResponseEntity.of(pd).build();
    }
}
