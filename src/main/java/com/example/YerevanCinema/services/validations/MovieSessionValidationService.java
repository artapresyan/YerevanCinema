package com.example.YerevanCinema.services.validations;

import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.MovieSessionAlreadyExistsException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MovieSessionValidationService {

    private final Logger logger = LogManager.getLogger();

    public void validateMovieSessionStart(LocalDateTime movieSessionStart, Hall hall)
            throws IOException, MovieSessionAlreadyExistsException {

        if (movieSessionStart == null || movieSessionStart.isBefore(LocalDateTime.now())) {
            logger.log(Level.ERROR, String.format("Check movie session start date and time, ' %s ' is not valid",
                    movieSessionStart));
            throw new IOException();
        }
        MovieSession movieSession = hall.getMovieSessions().stream().filter(registeredSession ->
                        (double) (movieSessionStart.getHour() - registeredSession.getMovieSessionStart().getHour()) < 1.5)
                .findAny().orElse(null);
        if (movieSession != null) {
            logger.log(Level.ERROR, String.format("There is already registered session at ' %1$s '. Hall is occupied by %2$s session",
                    movieSessionStart, movieSession.getMovie().getMovieName()));
            throw new MovieSessionAlreadyExistsException("Movie Session exists");
        }
    }

    public void validateMovieSessionEnd(LocalDateTime movieSessionStart, LocalDateTime movieSessionEnd)
            throws IOException {
        if (movieSessionEnd == null || movieSessionEnd.isBefore(movieSessionStart)) {
            logger.log(Level.ERROR, String.format("Check movie session end date and time. Start date and" +
                    " time is ' %1$s ', end date and time is ' %2$s '", movieSessionStart, movieSessionEnd));
            throw new IOException();
        }
    }

    public void validateMovieSessionPrice(Integer price) throws IOException{
        if (price == null || price < 2000) {
            logger.log(Level.WARN, String.format("Minimal price for session ticket is ' %1$s '. ' %2$s ' is invalid",
                    2000, price));
            throw new IOException();
        }
    }
}
