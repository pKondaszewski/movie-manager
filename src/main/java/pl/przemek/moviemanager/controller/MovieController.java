package pl.przemek.moviemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.przemek.moviemanager.dto.FavouriteMovieDTO;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.dto.MoviesSingleDTO;
import pl.przemek.moviemanager.exception.OmdbApiException;
import pl.przemek.moviemanager.service.MovieService;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping(path = "/single/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDTO> getMovieByIdOrTitle(@RequestParam(required = false) String omdbID,
                                                        @RequestParam(required = false) String movieTitle,
                                                        @RequestParam(required = false) MovieType typeOfResult,
                                                        @RequestParam(required = false) Year releaseYear) throws OmdbApiException {
        MovieDTO movieById = movieService.getMovieByIdOrTitle(omdbID, movieTitle, typeOfResult, releaseYear);
        return ResponseEntity.status(200).body(movieById);
    }

    @GetMapping(path = "/list/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MoviesSingleDTO>> getMoviesByTitleSearch(@RequestParam String movieTitle,
                                                                   @RequestParam(required = false) MovieType typeOfResult,
                                                                   @RequestParam(required = false) Year releaseYear) throws OmdbApiException {
        List<MoviesSingleDTO> moviesByTitleSearch = movieService.getMoviesByTitleSearch(movieTitle, typeOfResult, releaseYear);
        return ResponseEntity.status(200).body(moviesByTitleSearch);
    }

    @PostMapping(path = "/favourite", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FavouriteMovieDTO> saveFavouriteMovie(@RequestParam String omdbID) throws OmdbApiException {
        FavouriteMovieDTO favouriteMovieDTO = movieService.saveFavouriteMovie(omdbID);
        return ResponseEntity.status(201).body(favouriteMovieDTO);
    }

    @GetMapping(path = "/favourite", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FavouriteMovieDTO>> getAllFavouriteMovies() {
        List<FavouriteMovieDTO> allFavouriteMoviesDTOs = movieService.getAllFavouriteMovies();
        return ResponseEntity.status(200).body(allFavouriteMoviesDTOs);
    }
}