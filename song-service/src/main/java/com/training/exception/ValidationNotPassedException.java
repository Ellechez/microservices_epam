package com.training.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;


@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationNotPassedException extends RuntimeException{
    Map<String, String> details;
    public ValidationNotPassedException(String message, Map<String, String> details) {
        super(message);
        this.details = details;
    }
}
