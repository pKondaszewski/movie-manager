package pl.przemek.moviemanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.przemek.moviemanager.dto.FavouriteMovieDTO;
import pl.przemek.moviemanager.entity.FavouriteMovie;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface FavouriteMovieMapper {
    FavouriteMovieDTO favouriteMovieToFavouriteMovieDTO(FavouriteMovie favouriteMovie);
}
