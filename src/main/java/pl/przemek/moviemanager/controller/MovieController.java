package pl.przemek.moviemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.przemek.moviemanager.dto.MovieDTO;
import pl.przemek.moviemanager.dto.MoviesListDTO;
import pl.przemek.moviemanager.dto.MoviesSingleDTO;
import pl.przemek.moviemanager.exception.OmbdApiException;
import pl.przemek.moviemanager.service.MovieService;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping(path = "/single/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDTO> getMovieByIdOrTitle(@RequestParam(required = false) String id,
                                                        @RequestParam(required = false) String movieTitle,
                                                        @RequestParam(required = false) MovieType typeOfResult,
                                                        @RequestParam(required = false) Year releaseYear) throws OmbdApiException {
        MovieDTO movieById = movieService.getMovieByIdOrTitle(id, movieTitle, typeOfResult, releaseYear);
        return ResponseEntity.status(200).body(movieById);
    }

    @GetMapping(path = "/list/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MoviesSingleDTO>> getMoviesBySearch(@RequestParam String movieTitle,
                                                                   @RequestParam(required = false) MovieType typeOfResult,
                                                                   @RequestParam(required = false) Year releaseYear) throws OmbdApiException {
        MoviesListDTO moviesByTitle = movieService.getMovieByTitle(movieTitle, typeOfResult, releaseYear);
        return ResponseEntity.status(200).body(moviesByTitle.search());
    }
}