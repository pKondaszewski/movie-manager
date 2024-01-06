package pl.przemek.moviemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.przemek.moviemanager.entity.FavouriteMovie;

@Repository
public interface MovieRepository extends JpaRepository<FavouriteMovie, Integer> {
}
