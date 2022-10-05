package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.MovieSessionNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieSessionService {

    MovieSession getMovieSessionByID(Long movieSessionID) throws MovieSessionNotFoundException;

    List<MovieSession> getAllMovieSessions();

    MovieSession addMovieSession(LocalDateTime movieSessionStart, LocalDateTime movieSessionEnd, Integer movieSessionPrice,
                                 Hall hall, Movie movie, Admin admin, String password);

    MovieSession removeMovieSession(Admin admin, String password, Long movieSessionID);

    MovieSession updateMovieSession(Long movieSessionID, LocalDateTime movieSessionStart, LocalDateTime movieSessionEnd,
                                    Integer movieSessionPrice, Hall hall, Movie movie, Admin admin);

    List<MovieSession> getAllMovieSessionsByStart(LocalDateTime movieSessionStart);

    List<MovieSession> getAllMovieSessionsByPrice(Integer movieSessionPrice);
}
