package pl.przemek.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MoviesListDTO(
        List<MoviesSingleDTO> search,
        String response,
        String error) {
}
