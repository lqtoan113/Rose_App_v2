package com.rose.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class FireBaseException extends RuntimeException {
    private HttpStatus code;
    private String message;
}
