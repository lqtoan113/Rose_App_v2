package com.rose.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private HttpStatus code;
    private String message;
}
