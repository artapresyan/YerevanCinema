package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie getByMovieName(String movieName);
    Movie getByMovieCategory(String movieCategory);
}
