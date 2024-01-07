package pl.przemek.moviemanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.FavouriteMovieDTO;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.entity.FavouriteMovie;
import pl.przemek.moviemanager.exception.MovieNotFound;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.exception.TooManyResultsException;
import pl.przemek.moviemanager.mapper.FavouriteMovieMapper;
import pl.przemek.moviemanager.repository.MovieRepository;
import pl.przemek.moviemanager.service.omdb.api.OmdbApiClient;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final OmdbApiClient omdbApiClient;
    private final MovieRepository movieRepository;
    private final FavouriteMovieMapper favouriteMovieMapper;

    public MovieDTO getMovieByIdOrTitle(String omdbID, String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmdbApiException {
        return omdbApiClient.getMovieByIdOrTitle(omdbID, movieTitle, typeOfResult, releaseYear);
    }

    public MoviesListDTO getMoviesByTitleSearch(String title, MovieType typeOfResult, Year releaseYear) throws OmdbApiException {
        MoviesListDTO moviesByTitle = omdbApiClient.getMoviesByTitleSearch(title, typeOfResult, releaseYear);
        validateSearchResult(moviesByTitle);
        return moviesByTitle;
    }

    private void validateSearchResult(MoviesListDTO searchResult) throws OmdbApiException {
        boolean isResponseValid = Boolean.parseBoolean(searchResult.response());
        if (!isResponseValid) {
            String logLabel = "OmdbClient error message: {}";
            String errorMessage = searchResult.error();
            if (errorMessage.contains("Movie not found")) {
                log.error(logLabel, searchResult.error());
                throw new OmdbApiException(searchResult.error(), new MovieNotFound(), HttpStatus.NOT_FOUND);
            } else if (errorMessage.contains("Too many results")) {
                log.error(logLabel, searchResult.error());
                throw new OmdbApiException(searchResult.error(), new TooManyResultsException(), HttpStatus.PAYLOAD_TOO_LARGE);
            } else {
                log.error(logLabel, searchResult.error());
                throw new OmdbApiException(searchResult.error(), new InternalError(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Transactional
    public FavouriteMovieDTO saveFavouriteMovie(String omdbID) throws OmdbApiException {
        MovieDTO movieDTO = getMovieByIdOrTitle(omdbID, null, null, null);
        FavouriteMovie favouriteMovie = new FavouriteMovie(omdbID, movieDTO);
        movieRepository.save(favouriteMovie);
        return favouriteMovieMapper.favouriteMovieToFavouriteMovieDTO(favouriteMovie);
    }

    public List<FavouriteMovieDTO> getAllFavouriteMovies() {
        return movieRepository.findAll().stream()
                .map(favouriteMovieMapper::favouriteMovieToFavouriteMovieDTO)
                .toList();
    }
}