package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.MovieSessionAlreadyExistsException;
import com.example.YerevanCinema.exceptions.MovieSessionNotFoundException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.MovieSessionRepository;
import com.example.YerevanCinema.services.MovieSessionService;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MovieSessionServiceImpl implements MovieSessionService {

    private final MovieSessionRepository movieSessionRepository;
    private final Logger logger = LogManager.getLogger();

    public MovieSessionServiceImpl(MovieSessionRepository movieSessionRepository) {
        this.movieSessionRepository = movieSessionRepository;
    }

    @Override
    public MovieSession getMovieSessionByID(Long movieSessionID) throws MovieSessionNotFoundException {
        Optional<MovieSession> movieSession = movieSessionRepository.findById(movieSessionID);
        if (movieSession.isPresent()) {
            return movieSession.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get movie session" +
                    " by ' %s ' id", movieSessionID));
            throw new MovieSessionNotFoundException("Movie session not found");
        }
    }

    @Override
    public List<MovieSession> getAllMovieSessions() {
        return movieSessionRepository.findAll();
    }

    @Override
    public MovieSession addMovieSession(String movieSessionStart, String movieSessionEnd, Integer movieSessionPrice,
                                        Hall hall, Movie movie, Admin admin, String password, PasswordEncoder passwordEncoder,
                                        MovieSessionValidationService movieSessionValidationService) {
        try {
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                movieSessionValidationService.validateMovieSessionStart(movieSessionStart, hall);
                movieSessionValidationService.validateMovieSessionEnd(movieSessionStart, movieSessionEnd);
                movieSessionValidationService.validateMovieSessionPrice(movieSessionPrice);
                MovieSession movieSession = new MovieSession(movieSessionStart, movieSessionEnd, movieSessionPrice,
                        hall, movie, admin);
                movieSessionRepository.save(movieSession);
                return movieSession;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (WrongPasswordException | IOException | MovieSessionAlreadyExistsException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to add movie session");
            return null;
        }
    }

    @Override
    public MovieSession removeMovieSession(Admin admin, String password, Long movieSessionID, PasswordEncoder passwordEncoder) {
        try {
            MovieSession movieSession = getMovieSessionByID(movieSessionID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                movieSessionRepository.deleteById(movieSessionID);
                return movieSession;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (MovieSessionNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove" +
                    " movie session with ' %s ' id", movieSessionID));
            return null;
        }
    }

    @Override
    public MovieSession updateMovieSession(Long movieSessionID, String movieSessionStart, String movieSessionEnd,
                                           Integer movieSessionPrice, Hall hall, Movie movie, Admin admin,
                                           MovieSessionValidationService movieSessionValidationService) {
        try {
            MovieSession movieSession = getMovieSessionByID(movieSessionID);
            try {
                movieSessionValidationService.validateMovieSessionStart(movieSessionStart, hall);
                movieSession.setMovieSessionStart(movieSessionStart);
            } catch (IOException | MovieSessionAlreadyExistsException ignored) {
            }
            try {
                movieSessionValidationService.validateMovieSessionEnd(movieSessionStart, movieSessionEnd);
                movieSession.setMovieSessionEnd(movieSessionEnd);
            } catch (IOException ignored) {
            }
            try {
                movieSessionValidationService.validateMovieSessionPrice(movieSessionPrice);
                movieSession.setMovieSessionPrice(movieSessionPrice);
            } catch (IOException ignored) {
            }
            movieSession.setHall(hall);
            movieSession.setMovie(movie);
            movieSession.setAdmin(admin);
            movieSessionRepository.save(movieSession);
            return movieSession;
        } catch (MovieSessionNotFoundException e) {
            logger.log(Level.ERROR, String.format("Cannot get movie session with %s id to update information", movieSessionID));
            return null;
        }
    }

    @Override
    public List<MovieSession> getAllMovieSessionsByStart(String movieSessionStart) {
        return movieSessionRepository.getByMovieSessionStart(movieSessionStart);
    }

    @Override
    public List<MovieSession> getAllMovieSessionsByPrice(Integer movieSessionPrice) {
        return movieSessionRepository.getByMovieSessionPrice(movieSessionPrice);
    }
}
