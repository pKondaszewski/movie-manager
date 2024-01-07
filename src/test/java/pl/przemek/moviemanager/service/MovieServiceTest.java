package pl.przemek.moviemanager.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.*;
import pl.przemek.moviemanager.entity.FavouriteMovie;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.mapper.FavouriteMovieMapper;
import pl.przemek.moviemanager.repository.MovieRepository;
import pl.przemek.moviemanager.service.omdb.api.OmdbApiClient;
import pl.przemek.moviemanager.service.omdb.api.ResponseValidator;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private OmdbApiClient omdbApiClient;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private FavouriteMovieMapper favouriteMovieMapper;
    @Mock
    private ResponseValidator responseValidator;
    @InjectMocks
    private MovieService movieService;

    private static String randomOmdbId;
    private static String randomMovieTitle;
    private static MovieType randomMovieType;
    private static Year randomYear;
    private static MovieDTO randMovieDTO;
    private static MoviesListDTO randomMoviesListDTOs;
    private static MoviesListDTO movieNotFoundExceptionDTO;
    private static MoviesListDTO tooManyResultsExceptionDTO;
    private static MoviesListDTO internalServerExceptionDTO;
    private static FavouriteMovie randomFavouriteMovie;
    private static FavouriteMovieDTO randomFavouriteMovieDTO;

    @BeforeAll
    static void initVars() {
        randomOmdbId = "123";
        randomMovieTitle = "Movie Title";
        randomMovieType = MovieType.MOVIE;
        randomYear = Year.of(2022);

        randMovieDTO = new MovieDTO("title", "plot", "genre", "director", "poster");
        randomMoviesListDTOs = new MoviesListDTO(
                List.of(new MoviesSingleDTO("title", "year", "imdbId", "type", "poster"),
                        new MoviesSingleDTO("title2", "year2", "imdbId2", "type2", "poster2")),
                "True", null
        );
        movieNotFoundExceptionDTO = new MoviesListDTO(null, "False", "Movie not found!");
        tooManyResultsExceptionDTO = new MoviesListDTO(null, "False", "Too many results.");
        internalServerExceptionDTO = new MoviesListDTO(null, "False", "Internal server error.");
        randomFavouriteMovie = new FavouriteMovie(null, randMovieDTO);
        randomFavouriteMovieDTO = new FavouriteMovieDTO(randomOmdbId, randMovieDTO.title(), randMovieDTO.plot(), randMovieDTO.genre(), randMovieDTO.director(), randMovieDTO.poster());
    }

    @Test
    void shouldReturnCorrectMovieDtoWhenGetMovieById() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenReturn(new MovieWithResponseStatusDTO(randMovieDTO));

        // Act
        MovieDTO result = movieService.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear);

        // Assert
        assertEquals(randMovieDTO, result);
    }

    @Test
    void shouldPassExceptionWhenClientThrowsOneDuringAskingForSingleDTO() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenThrow(OmdbApiException.class);

        // Act & Assert
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

        // Act & Assert
        assertThrows(OmdbApiException.class,
                () -> movieService.getMovieByIdOrTitle(null, randomMovieTitle, randomMovieType, randomYear));
    }

    @Test
    void shouldThrowExceptionWhenClientReturnMovieNotFoundResponse() throws Exception {
        // Arrange
        when(omdbApiClient.getMoviesByTitleSearch(any(), any(), any())).thenReturn(movieNotFoundExceptionDTO);
        doThrow(new OmdbApiException(null, null, HttpStatus.NOT_FOUND)).when(responseValidator).validateSearchResult(any());

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
        doThrow(new OmdbApiException(null, null, HttpStatus.PAYLOAD_TOO_LARGE)).when(responseValidator).validateSearchResult(any());

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
        doThrow(new OmdbApiException(null, null, HttpStatus.INTERNAL_SERVER_ERROR)).when(responseValidator).validateSearchResult(any());

        // Act
        OmdbApiException exception = assertThrows(OmdbApiException.class,
                () -> movieService.getMoviesByTitleSearch(randomMovieTitle, randomMovieType, randomYear));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    void shouldSaveAndReturnFavouriteMovieDTO() throws Exception {
        // Arrange
        FavouriteMovieDTO expectedResult = new FavouriteMovieDTO(
                randomOmdbId, randMovieDTO.title(), randMovieDTO.plot(), randMovieDTO.genre(), randMovieDTO.director(), randMovieDTO.poster());
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenReturn(new MovieWithResponseStatusDTO(randMovieDTO));
        when(favouriteMovieMapper.favouriteMovieToFavouriteMovieDTO(any())).thenReturn(expectedResult);

        // Act
        FavouriteMovieDTO favouriteMovieDTO = movieService.saveFavouriteMovie(randomOmdbId);

        // Assert
        verify(movieRepository, times(1)).save(any(FavouriteMovie.class));
        verify(favouriteMovieMapper, times(1)).favouriteMovieToFavouriteMovieDTO(any(FavouriteMovie.class));
        assertEquals(expectedResult, favouriteMovieDTO);
    }

    @Test
    void shouldPassExceptionWhenClientThrowsOneDuringAskingForSingleMovie() throws Exception {
        // Arrange
        when(omdbApiClient.getMovieByIdOrTitle(any(), any(), any(), any())).thenThrow(OmdbApiException.class);

        // Act & Assert
        assertThrows(OmdbApiException.class,
                () -> movieService.saveFavouriteMovie(randomOmdbId));
    }

    @Test
    void shouldListFavouriteMoviesDTOs() {
        // Arrange
        List<FavouriteMovie> mockFavouriteMovies = List.of(randomFavouriteMovie);
        List<FavouriteMovieDTO> expectedFavouriteMovieDTOs = List.of(randomFavouriteMovieDTO);

        when(movieRepository.findAll()).thenReturn(mockFavouriteMovies);
        for (int i = 0; i < mockFavouriteMovies.size(); i++) {
            when(favouriteMovieMapper.favouriteMovieToFavouriteMovieDTO(mockFavouriteMovies.get(i)))
                    .thenReturn(expectedFavouriteMovieDTOs.get(i));
        }

        // Act
        List<FavouriteMovieDTO> result = movieService.getAllFavouriteMovies();

        // Assert
        assertEquals(expectedFavouriteMovieDTOs, result);
        verify(movieRepository, times(1)).findAll();
        for (FavouriteMovie movie : mockFavouriteMovies) {
            verify(favouriteMovieMapper, times(1)).favouriteMovieToFavouriteMovieDTO(movie);
        }
    }
}
