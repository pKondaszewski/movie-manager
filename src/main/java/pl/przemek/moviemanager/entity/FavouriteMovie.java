package pl.przemek.moviemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.przemek.moviemanager.dto.MovieDTO;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FavouriteMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String omdbID;
    private String title;
    private String plot;
    private String genre;
    private String director;
    private String poster;

    public FavouriteMovie(String omdbID, MovieDTO movieDTO) {
        this.omdbID = omdbID;
        this.title = movieDTO.title();
        this.plot = movieDTO.plot();
        this.genre = movieDTO.director();
        this.director = movieDTO.director();
        this.poster = movieDTO.poster();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, omdbID, title, plot, genre, director, poster);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FavouriteMovie that = (FavouriteMovie) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(omdbID, that.omdbID) &&
                Objects.equals(title, that.title) &&
                Objects.equals(plot, that.plot) &&
                Objects.equals(genre, that.genre) &&
                Objects.equals(director, that.director) &&
                Objects.equals(poster, that.poster);
    }
}
