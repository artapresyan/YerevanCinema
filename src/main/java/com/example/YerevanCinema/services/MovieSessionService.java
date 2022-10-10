package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.MovieSessionNotFoundException;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface MovieSessionService {

    MovieSession getMovieSessionByID(Long movieSessionID) throws MovieSessionNotFoundException;

    List<MovieSession> getAllMovieSessions();

    MovieSession addMovieSession(String movieSessionStart, String movieSessionEnd, Integer movieSessionPrice,
                                 Hall hall, Movie movie, Admin admin, String password, PasswordEncoder passwordEncoder,
                                 MovieSessionValidationService movieSessionValidationService);

    MovieSession removeMovieSession(Admin admin, String password, Long movieSessionID, PasswordEncoder passwordEncoder);

    MovieSession updateMovieSession(Long movieSessionID, String movieSessionStart, String movieSessionEnd,
                                    Integer movieSessionPrice, Hall hall, Movie movie, Admin admin,
                                    MovieSessionValidationService movieSessionValidationService);

    List<MovieSession> getAllMovieSessionsByStart(String movieSessionStart);

    List<MovieSession> getAllMovieSessionsByPrice(Integer movieSessionPrice);

    List<MovieSession> deleteAllMovieSessionsByMovieID(Long movieID);
}
