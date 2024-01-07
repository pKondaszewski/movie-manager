package pl.przemek.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieWithResponseStatusDTO(String title,
                                         String plot,
                                         String genre,
                                         String director,
                                         String poster,
                                         String response,
                                         String error) {
    public MovieWithResponseStatusDTO(MovieDTO dto) {
        this(dto.title(), dto.plot(), dto.genre(), dto.director(), dto.poster(), null, null);
    }
}
