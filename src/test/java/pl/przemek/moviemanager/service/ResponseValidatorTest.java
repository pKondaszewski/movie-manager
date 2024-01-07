package pl.przemek.moviemanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.przemek.moviemanager.dto.MovieWithResponseStatusDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.service.omdb.api.ResponseValidator;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseValidatorTest {

    private final ResponseValidator responseValidator = new ResponseValidator();

    @Test
    void shouldThrowExceptionWhenSingleResponseIsFalse() {
        // Arrange
        var negativeResponseSingleSearchResult = new MovieWithResponseStatusDTO(null, null, null, null, null, "False", "error message");

        // Act & Assert
        assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(negativeResponseSingleSearchResult));
    }

    @Test
    void shouldNotThrowExceptionWhenSingleResponseIsTrue() {
        // Arrange
        var positiveResponseSingleSearchResult = new MovieWithResponseStatusDTO("title", "plot", "genre", "director", "poster", "True", null);

        // Act & Assert
        assertDoesNotThrow(() -> responseValidator.validateSearchResult(positiveResponseSingleSearchResult));
    }

    @Test
    void shouldThrowExceptionWhenListResponseIsFalse() {
        // Arrange
        var negativeResponseSingleSearchResult = new MoviesListDTO(null, "False", "error message");

        // Act & Assert
        assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(negativeResponseSingleSearchResult));
    }

    @Test
    void shouldNotThrowExceptionWhenListResponseIsTrue() {
        // Arrange
        var positiveResponseSingleSearchResult = new MoviesListDTO(null, "True", null);

        // Act & Assert
        assertDoesNotThrow(() -> responseValidator.validateSearchResult(positiveResponseSingleSearchResult));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSingleResponseIsFalse() {
        // Arrange
        var negativeResponseSingleSearchResult = new MovieWithResponseStatusDTO(null, null, null, null, null, "False", "Movie not found!");

        // Act
        OmdbApiException omdbApiException = assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(negativeResponseSingleSearchResult));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, omdbApiException.getHttpStatus());
    }

    @Test
    void shouldThrowPayloadTooLargeExceptionWhenSingleResponseIsFalse() {
        // Arrange
        var negativeResponseSingleSearchResult = new MovieWithResponseStatusDTO(null, null, null, null, null, "False", "Too many results");

        // Act
        OmdbApiException omdbApiException = assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(negativeResponseSingleSearchResult));
        // Assert
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, omdbApiException.getHttpStatus());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenSingleResponseIsFalse() {
        // Arrange
        var negativeResponseSingleSearchResult = new MovieWithResponseStatusDTO(null, null, null, null, null, "False", "Incorrect IMDb ID");

        // Act
        OmdbApiException omdbApiException = assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(negativeResponseSingleSearchResult));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, omdbApiException.getHttpStatus());
    }

    @Test
    void shouldThrowInternalServerErrorExceptionWhenSingleResponseIsFalse() {
        // Act
        OmdbApiException omdbApiException = assertThrows(OmdbApiException.class,
                () -> responseValidator.validateSearchResult(new Object()));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, omdbApiException.getHttpStatus());
    }
}
