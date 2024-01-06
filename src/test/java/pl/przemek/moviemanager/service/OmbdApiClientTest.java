package pl.przemek.moviemanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.dto.MoviesSingleDTO;
import pl.przemek.moviemanager.service.ombd.api.OmbdApiClient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OmbdApiClientTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private OmbdApiClient omdbApiClient;

    private String randomId;
    private String randomMovieTitle;
    private MovieType randomMovieType;
    private Year randomYear;
    private String sampleJsonResponseSingleDTO;
    private String sampleJsonResponseListDTOs;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(omdbApiClient, "apiUrl", "http://mocked-api-url");
        ReflectionTestUtils.setField(omdbApiClient, "apiKey", "test-api-key");


        randomId = "123";
        randomMovieTitle = "Movie Title";
        randomMovieType = MovieType.MOVIE;
        randomYear = Year.of(2022);

        sampleJsonResponseSingleDTO = "{\"Title\":\"Movie Title\", \"Plot\":\"Movie Plot\", \"Genre\":\"Action\", \"Director\":\"Director Name\"}";
        sampleJsonResponseListDTOs = """
                {
                  "Search": [
                    {"Title": "Movie Title 1", "Year": "2022", "imdbID": "tt1234567", "Type": "Movie"},
                    {"Title": "Movie Title 2", "Year": "2022", "imdbID": "tt2345678", "Type": "Movie"},
                    {"Title": "Movie Title 3", "Year": "2022", "imdbID": "tt3456789", "Type": "Movie"}
                  ],
                  "totalResults": "3",
                  "Response": "True"
                }""";

    }

    @Test
    void shouldReturnCorrectMovieDtoWhenGetMovieById() throws Exception {
        // Arrange
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(sampleJsonResponseSingleDTO);

        // Act
        MovieDTO result = omdbApiClient.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear);

        // Assert
        checkResultDTO(result);
    }

    @Test
    void shouldReturnCorrectMovieDtoWhenGetMovieByTitle() throws Exception {
        // Arrange
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(sampleJsonResponseSingleDTO);

        // Act
        MovieDTO result = omdbApiClient.getMovieByIdOrTitle(randomId, null, randomMovieType, randomYear);

        // Assert
        checkResultDTO(result);
    }

    @Test
    void shouldThrowExceptionWhenGetMovieByNullIdAndTitle() {
        // Act
        Exception exception = assertThrows(Exception.class, () ->
                omdbApiClient.getMovieByIdOrTitle(null, null, randomMovieType, randomYear)
        );

        // Assert
        assertEquals("At least one of 'id' or 'movieTitle' is required.", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectMoviesDtosWhenGetMoviesByTitle() throws Exception {
        // Arrange
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(sampleJsonResponseListDTOs);

        // Act
        MoviesListDTO result = omdbApiClient.getMoviesByTitle(randomMovieTitle, randomMovieType, randomYear);

        // Assert
        checkResultDTOs(result);
    }

    @Test
    void checkUnexpectedExceptionsHandlingForSingleMovie() throws Exception {
        // Arrange
        String exceptionMessage = "Lorem ipsum";
        when(httpClient.send(any(), any())).thenThrow(new IOException(exceptionMessage));

        // Act
        Exception exception = assertThrows(Exception.class, () ->
                omdbApiClient.getMovieByIdOrTitle(randomId, randomMovieTitle, randomMovieType, randomYear)
        );

        // Assert
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void checkUnexpectedExceptionsHandlingForListOfMovies() throws Exception {
        // Arrange
        String exceptionMessage = "Lorem ipsum";
        when(httpClient.send(any(), any())).thenThrow(new IOException(exceptionMessage));

        // Act
        Exception exception = assertThrows(Exception.class, () ->
                omdbApiClient.getMoviesByTitle(randomMovieTitle, randomMovieType, randomYear)
        );

        // Assert
        assertEquals(exceptionMessage, exception.getMessage());
    }

    private void checkResultDTO(MovieDTO result) {
        assertEquals("Movie Title", result.title());
        assertEquals("Movie Plot", result.plot());
        assertEquals("Action", result.genre());
        assertEquals("Director Name", result.director());
        assertNull(result.poster());
    }

    private void checkResultDTOs(MoviesListDTO result) {
        List<MoviesSingleDTO> search = result.search();
        assertEquals("tt1234567", search.get(0).imdbID());
        assertEquals("Movie", search.get(1).type());
        assertEquals("2022", search.get(2).year());
    }
}
