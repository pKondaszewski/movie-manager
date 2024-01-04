package pl.przemek.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MoviesSingleDTO (String title,
                               String year,
                               @JsonProperty("imdbID") String imdbID,
                               String type,
                               String poster) {
}
