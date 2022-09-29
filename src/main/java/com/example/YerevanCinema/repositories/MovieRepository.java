package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie getByMovieName(String movieName);
    List<Movie> getByMovieCategory(String movieCategory);
}
