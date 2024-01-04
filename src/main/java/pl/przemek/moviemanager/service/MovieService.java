package pl.przemek.moviemanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.przemek.moviemanager.controller.MovieType;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.exception.OmbdApiException;
import pl.przemek.moviemanager.service.ombd.api.OmbdApiClient;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final OmbdApiClient ombdApiClient;

    public MovieDTO getMovieByIdOrTitle(String id, String movieTitle, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        return ombdApiClient.getMovieByIdOrTitle(id, movieTitle, typeOfResult, releaseYear);
    }

    public MoviesListDTO getMovieByTitle(String title, MovieType typeOfResult, Year releaseYear) throws OmbdApiException {
        return ombdApiClient.getMoviesByTitle(title, typeOfResult, releaseYear);
    }
}