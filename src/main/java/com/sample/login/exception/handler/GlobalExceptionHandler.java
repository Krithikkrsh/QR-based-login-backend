package com.sample.login.exception.handler;

import com.sample.login.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<String> handleInternalException(InternalException e){
        log.error("Internal Exception occurred",e);
        return ResponseEntity.ok("Internal Exception");
    }

}
