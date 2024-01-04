package pl.przemek.moviemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.przemek.moviemanager.exception.OmbdApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(OmbdApiException.class)
    public ResponseEntity<String> ombdApiExceptionHandler(OmbdApiException e) {
        if (BAD_REQUEST.equals(e.getHttpStatus())) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}