package pl.przemek.moviemanager.service.omdb.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pl.przemek.moviemanager.dto.MovieWithResponseStatusDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.exception.MovieNotFound;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.exception.TooManyResultsException;

@Component
@Slf4j
public class ResponseValidator {

    public <T> void validateSearchResult(T searchResult) throws OmdbApiException {
        if (searchResult instanceof MovieWithResponseStatusDTO singleSearchResult) {
            validateSingleSearchResult(singleSearchResult);
        } else if (searchResult instanceof MoviesListDTO listSearchResult) {
            validateListSearchResult(listSearchResult);
        } else {
            log.error("Unsupported search result type: {}", searchResult.getClass().getName());
            throw new OmdbApiException("Unsupported search result type", new InternalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateSingleSearchResult(MovieWithResponseStatusDTO searchResult) throws OmdbApiException {
        boolean isResponseValid = Boolean.parseBoolean(searchResult.response());
        if (!isResponseValid) {
            handleErrorResponse(searchResult.error());
        }
    }

    private void validateListSearchResult(MoviesListDTO searchResult) throws OmdbApiException {
        boolean isResponseValid = Boolean.parseBoolean(searchResult.response());
        if (!isResponseValid) {
            handleErrorResponse(searchResult.error());
        }
    }

    private void handleErrorResponse(String errorMessage) throws OmdbApiException {
        String logLabel = "OmdbClient error message: {}";
        log.error(logLabel, errorMessage);

        if (errorMessage.contains("Incorrect IMDb ID")) {
            throw new OmdbApiException(errorMessage, new MovieNotFound(), HttpStatus.BAD_REQUEST);
        } else if (errorMessage.contains("Movie not found")) {
            throw new OmdbApiException(errorMessage, new MovieNotFound(), HttpStatus.NOT_FOUND);
        } else if (errorMessage.contains("Too many results")) {
            throw new OmdbApiException(errorMessage, new TooManyResultsException(), HttpStatus.PAYLOAD_TOO_LARGE);
        } else {
            throw new OmdbApiException(errorMessage, new InternalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
