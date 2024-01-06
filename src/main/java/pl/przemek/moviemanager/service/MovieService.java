package pl.przemek.moviemanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.FavouriteMovieDTO;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.entity.FavouriteMovie;
import pl.przemek.moviemanager.exception.OmbdApiException;
import pl.przemek.moviemanager.mapper.FavouriteMovieMapper;
import pl.przemek.moviemanager.repository.MovieRepository;
import pl.przemek.moviemanager.service.ombd.api.OmbdApiClient;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final OmbdApiClient ombdApiClient;
    private final MovieRepository movieRepository;
    private final FavouriteMovieMapper favouriteMovieMapper;

    public MovieDTO getMovieByIdOrTitle(String ombdID, String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        return ombdApiClient.getMovieByIdOrTitle(ombdID, movieTitle, typeOfResult, releaseYear);
    }

    public MoviesListDTO getMovieByTitle(String title, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        return ombdApiClient.getMoviesByTitle(title, typeOfResult, releaseYear);
    }

    @Transactional
    public FavouriteMovieDTO saveFavouriteMovie(String ombdID) throws OmbdApiException {
        MovieDTO movieDTO = getMovieByIdOrTitle(ombdID, null, null, null);
        FavouriteMovie favouriteMovie = new FavouriteMovie(ombdID, movieDTO);
        movieRepository.save(favouriteMovie);
        return favouriteMovieMapper.favouriteMovieToFavouriteMovieDTO(favouriteMovie);
    }

    public List<FavouriteMovieDTO> getAllFavouriteMovies() {
        return movieRepository.findAll().stream()
                .map(favouriteMovieMapper::favouriteMovieToFavouriteMovieDTO)
                .toList();
    }
}