package pl.przemek.moviemanager.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class OmdbApiException extends Exception {

    private HttpStatus httpStatus;

    public OmdbApiException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
