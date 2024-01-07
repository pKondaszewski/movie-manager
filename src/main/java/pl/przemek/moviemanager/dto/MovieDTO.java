package pl.przemek.moviemanager.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDTO(String title,
                       String plot,
                       String genre,
                       String director,
                       String poster) {
    public MovieDTO(MovieWithResponseStatusDTO dto) {
        this(dto.title(), dto.plot(), dto.genre(), dto.director(), dto.poster());
    }
}