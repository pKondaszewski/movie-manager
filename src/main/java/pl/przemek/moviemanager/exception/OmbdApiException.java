package pl.przemek.moviemanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OmbdApiException extends Exception {

    private HttpStatus httpStatus;

    public OmbdApiException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
