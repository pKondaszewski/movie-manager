package pl.przemek.moviemanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class MovieService {

    private final OmdbApiClient omdbApiClient;
    private final MovieRepository movieRepository;
    private final FavouriteMovieMapper favouriteMovieMapper;
    private final ResponseValidator responseValidator;

    public MovieDTO getMovieByIdOrTitle(String omdbID, String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmdbApiException {
        MovieWithResponseStatusDTO movieByIdOrTitle = omdbApiClient.getMovieByIdOrTitle(omdbID, movieTitle, typeOfResult, releaseYear);
        responseValidator.validateSearchResult(movieByIdOrTitle);
        return new MovieDTO(movieByIdOrTitle);
    }

    public List<MoviesSingleDTO> getMoviesByTitleSearch(String title, MovieType typeOfResult, Year releaseYear) throws OmdbApiException {
        MoviesListDTO moviesByTitle = omdbApiClient.getMoviesByTitleSearch(title, typeOfResult, releaseYear);
        responseValidator.validateSearchResult(moviesByTitle);
        return moviesByTitle.search();
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