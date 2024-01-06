package pl.przemek.moviemanager.dto;

public record FavouriteMovieDTO(String omdbID,
                                String title,
                                String plot,
                                String genre,
                                String director,
                                String poster) {
}
