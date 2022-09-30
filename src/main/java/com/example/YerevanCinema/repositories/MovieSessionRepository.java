package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {

    List<MovieSession> getByMovieSessionStart(LocalDateTime movieSessionStart);

    List<MovieSession> getByMovieSessionPrice(Integer movieSessionPrice);
}
