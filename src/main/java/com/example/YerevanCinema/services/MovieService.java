package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.NoSuchUserException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.MovieRepository;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieValidationService movieValidationService;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger();

    public MovieService(MovieRepository movieRepository, MovieValidationService movieValidationService,
                        AdminService adminService, PasswordEncoder passwordEncoder) {
        this.movieRepository = movieRepository;
        this.movieValidationService = movieValidationService;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    public Movie getMovieByID(Long movieID) throws MovieNotFoundException {
        Optional<Movie> movie = movieRepository.findById(movieID);
        if (movie.isPresent()) {
            return movie.get();
        } else
            throw new MovieNotFoundException(String.format("No movie found with %s id", movieID));
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Long adminID, String password, String movieName, String movieCategory,
                          String movieDescription, String movieLanguage) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                try {
                    movieValidationService.validateMovieName(movieName);
                    movieValidationService.validateMovieCategory(movieCategory);
                    movieValidationService.validateMovieDescription(movieDescription);
                    movieValidationService.validateMovieLanguage(movieLanguage);
                } catch (IOException e) {
                    return null;
                }
                Movie movie = new Movie(movieName, movieCategory, movieDescription, movieLanguage);
                movieRepository.save(movie);
                return movie;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException e) {
            logger.log(Level.ERROR, e);
            return null;
        }
    }

    public Movie removeMovie(Long movieID, Long adminID, String password) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Movie movie = getMovieByID(movieID);
                movieRepository.deleteById(movieID);
                return movie;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException | MovieNotFoundException e) {
            logger.log(Level.ERROR, e);
            return null;
        }
    }

    public Movie updateMovie(Long movieID, String movieName, String movieCategory,
                             String movieDescription, String movieLanguage) {
        try {
            Movie movie = getMovieByID(movieID);
            try {
                movieValidationService.validateMovieName(movieName);
                movie.setMovieName(movieName);
            } catch (IOException ignored) {
            }
            try {
                movieValidationService.validateMovieCategory(movieCategory);
                movie.setMovieCategory(movieCategory);
            } catch (IOException ignored) {
            }
            try {
                movieValidationService.validateMovieDescription(movieDescription);
                movie.setMovieDescription(movieDescription);
            } catch (IOException ignored) {
            }
            try {
                movieValidationService.validateMovieLanguage(movieLanguage);
                movie.setMovieLanguage(movieLanguage);
            } catch (IOException ignored) {
            }
            movieRepository.save(movie);
            return movie;
        } catch (MovieNotFoundException e) {
            logger.log(Level.ERROR, e);
            return null;
        }

    }

    public List<Movie> getMoviesByCategory(String movieCategory) {
        return movieRepository.getByMovieCategory(movieCategory);
    }

    public Movie getMovieByName(String movieName) throws MovieNotFoundException {
        Movie movie = movieRepository.getByMovieName(movieName);
        if (movie != null) {
            return movie;
        } else {
            logger.log(Level.ERROR, String.format("Unknown movie: %s", movieName));
            throw new MovieNotFoundException(String.format("No movie found with ' %s ' name", movieName));
        }
    }
}
