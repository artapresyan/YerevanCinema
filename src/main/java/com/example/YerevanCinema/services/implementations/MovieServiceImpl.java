package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.MovieRepository;
import com.example.YerevanCinema.services.MovieService;
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
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final Logger logger = LogManager.getLogger();

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie getMovieByID(Long movieID) throws MovieNotFoundException {
        Optional<Movie> movie = movieRepository.findById(movieID);
        if (movie.isPresent()) {
            return movie.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get movie by ' %s ' id", movieID));
            throw new MovieNotFoundException("Movie not found");
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie addMovie(Long adminID, String password, String movieName, String movieCategory,
                          String movieDescription, String movieLanguage, AdminServiceImpl adminService,
                          PasswordEncoder passwordEncoder, MovieValidationService movieValidationService) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                try {
                    movieValidationService.validateMovieName(movieName);
                    movieValidationService.validateMovieCategory(movieCategory);
                    if (movieDescription != null)
                        movieValidationService.validateMovieDescription(movieDescription);
                    if (movieLanguage != null)
                        movieValidationService.validateMovieLanguage(movieLanguage);
                } catch (IOException e) {
                    return null;
                }
                Movie movie = new Movie(movieName, movieCategory, movieDescription, movieLanguage);
                movieRepository.save(movie);
                return movie;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to add movie");
            return null;
        }
    }

    @Override
    public Movie removeMovie(Long movieID, Long adminID, String password, AdminServiceImpl adminService,
                             PasswordEncoder passwordEncoder) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Movie movie = getMovieByID(movieID);
                movieRepository.deleteById(movieID);
                return movie;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException | MovieNotFoundException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove" +
                    " movie with ' %s ' id", movieID));
            return null;
        }
    }

    @Override
    public Movie updateMovie(Long movieID, String movieName, String movieCategory,
                             String movieDescription, String movieLanguage, MovieValidationService movieValidationService) {
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
            logger.log(Level.ERROR, String.format("Cannot get movie with %s id to update information", movieID));
            return null;
        }
    }

    @Override
    public List<Movie> getMoviesByCategory(String movieCategory) {
        return movieRepository.getByMovieCategory(movieCategory);
    }

    @Override
    public Movie getMovieByName(String movieName) throws MovieNotFoundException {
        Movie movie = movieRepository.getByMovieName(movieName);
        if (movie != null) {
            return movie;
        } else {
            logger.log(Level.ERROR, String.format("Cannot get movie with %s name", movieName));
            throw new MovieNotFoundException("Movie not found");
        }
    }
}
