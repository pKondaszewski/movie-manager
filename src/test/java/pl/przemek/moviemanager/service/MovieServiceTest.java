package pl.przemek.moviemanager.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.dto.MoviesSingleDTO;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.mapper.FavouriteMovieMapper;
import pl.przemek.moviemanager.repository.MovieRepository;
import pl.przemek.moviemanager.service.omdb.api.OmdbApiClient;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private OmdbApiClient omdbApiClient;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private FavouriteMovieMapper favouriteMovieMapper;
    @InjectMocks
    private MovieService movieService;
    
    private static String randomMovieTitle;
    private static MovieType randomMovieType;
    private static Year randomYear;
    private static MovieDTO randomMovieDTO;
    private static MoviesListDTO randomMoviesListDTOs;
    private static MoviesListDTO movieNotFoundExceptionDTO;
    private static MoviesListDTO tooManyResultsExceptionDTO;
    private static MoviesListDTO internalServerExceptionDTO;

    @BeforeAll
    static void initVars() {
        randomMovieTitle = "Movie Title";
        randomMovieType = MovieType.MOVIE;
        randomYear = Year.of(2022);

        randomMovieDTO = new MovieDTO("title", "plot", "genre", "director", "poster");
        randomMoviesListDTOs = new MoviesListDTO(
                List.of(new MoviesSingleDTO("title", "year", "imdbId", "type", "poster"),
                        new MoviesSingleDTO("title2", "year2", "imdbId2", "type2", "poster2")),
                "True", null
        );
        movieNotFoundExceptionDTO = new MoviesListDTO(null, "False", "Movie not found!");
        tooManyResultsExceptionDTO = new MoviesListDTO(null, "False", "Too many results.");
        internalServerExceptionDTO = new MoviesListDTO(null, "False", "Internal server error.");
    }

    @Test
    void shouldReturnCorrectMovieDtoWhenGetMovieById() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenReturn(randomMovieDTO);

        // Act
        MovieDTO result = movieService.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear);

        // Assert
        assertEquals(randomMovieDTO, result);
    }

    @Test
    void shouldPassExceptionWhenClientThrowsOneDuringAskingForSingleDTO() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenThrow(OmdbApiException.class);

        // Assert
        assertThrows(OmdbApiException.class,
                () -> movieService.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear));
    }

    @Test
    void shouldReturnCorrectMoviesDtoWhenGetMoviesByTitleSearch() throws Exception {
        // Arrange
        when(omdbApiClient.getMoviesByTitleSearch(any(), any(), any())).thenReturn(randomMoviesListDTOs);

        // Act
        MoviesListDTO result = movieService.getMoviesByTitleSearch(randomMovieTitle, randomMovieType, randomYear);

        // Assert
        assertEquals(randomMoviesListDTOs, result);
    }

    @Test
    void shouldPassExceptionWhenClientThrowsOneDuringAskingForListOfDTOs() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenThrow(OmdbApiException.class);

        // Assert
        assertThrows(OmdbApiException.class,
                () -> movieService.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear));
    }

    @Test
    void shouldThrowExceptionWhenClientReturnMovieNotFoundResponse() throws Exception {
        // Arrange
        when(omdbApiClient.getMoviesByTitleSearch(any(), any(), any())).thenReturn(movieNotFoundExceptionDTO);

        // Act
        OmdbApiException exception = assertThrows(OmdbApiException.class,
                () -> movieService.getMoviesByTitleSearch(randomMovieTitle, randomMovieType, randomYear));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void shouldThrowExceptionWhenClientReturnTooManyResultsResponse() throws Exception {
        // Arrange
        when(omdbApiClient.getMoviesByTitleSearch(any(), any(), any())).thenReturn(tooManyResultsExceptionDTO);

        // Act
        OmdbApiException exception = assertThrows(OmdbApiException.class,
                () -> movieService.getMoviesByTitleSearch(randomMovieTitle, randomMovieType, randomYear));

        // Assert
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getHttpStatus());
    }

    @Test
    void shouldThrowExceptionWhenClientReturnUnknownResponse() throws Exception {
        // Arrange
        when(omdbApiClient.getMoviesByTitleSearch(any(), any(), any())).thenReturn(internalServerExceptionDTO);

        // Act
        OmdbApiException exception = assertThrows(OmdbApiException.class,
                () -> movieService.getMoviesByTitleSearch(randomMovieTitle, randomMovieType, randomYear));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }
}
