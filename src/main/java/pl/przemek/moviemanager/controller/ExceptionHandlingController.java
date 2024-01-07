package pl.przemek.moviemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.przemek.moviemanager.exception.OmdbApiException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(OmdbApiException.class)
    public ResponseEntity<String> omdbApiExceptionHandler(OmdbApiException e) {
        return switch (e.getHttpStatus()) {
            case PAYLOAD_TOO_LARGE -> ResponseEntity.status(PAYLOAD_TOO_LARGE).body(e.getMessage());
            case BAD_REQUEST -> ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
            case NOT_FOUND -> ResponseEntity.status(NOT_FOUND).body(e.getMessage());
            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        };
    }
}