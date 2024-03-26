package com.rose.exceptions;

import com.rose.models.ResponseObject;
import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseObject> handleProductException(CustomException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(e.getCode()).body(new ResponseObject(e.getCode().toString(), e.getMessage(), null, null));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseObject> handle404Exception(FileNotFoundException e) {
        LOGGER.error(e.getMessage());
        return ResponseEntity.status(404).body(new ResponseObject("NOT FOUND","Resource not found", null, null));
    }
    @ExceptionHandler(FireBaseException.class)
    public ResponseEntity<ResponseObject> handleFireBaseException(FireBaseException e) {
        LOGGER.error(e.getStackTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject(e.getCode().toString(), e.getMessage(), null, null)
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseObject> handleUnwantedException(Exception e) {
        LOGGER.error(e.getClass());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObject(HttpStatus.FORBIDDEN.toString(), e.getMessage(), null, null));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseObject> handleBindingException(BindException e){
        List<String> listErrors = new ArrayList<>();
        for (FieldError i: e.getFieldErrors()) {
            listErrors.add("@"+ i.getField().toUpperCase() +" : "+ i.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject(HttpStatus.BAD_REQUEST.toString(), listErrors.toString(), null, null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseObject> handleBadCredentialsException(BadCredentialsException e) {
        LOGGER.error(e.getClass());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("BAD_REQUEST", "Wrong username or password", null, null));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseObject> handleJwtException(JwtException e) {
        LOGGER.error(e.getClass());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ResponseObject("UNAUTHORIZED", "JWT token is expired", null, null));
    }
}
