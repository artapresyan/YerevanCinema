package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface MovieService {

    Movie getMovieByID(Long movieID) throws MovieNotFoundException;

    List<Movie> getAllMovies();

    Movie addMovie(Long adminID, String password, String movieName, String movieCategory,
                   String movieDescription, String movieLanguage, AdminServiceImpl adminService,
                   PasswordEncoder passwordEncoder, MovieValidationService movieValidationService);

    Movie removeMovie(Long movieID, Long adminID, String password,AdminServiceImpl adminService, PasswordEncoder passwordEncoder);

    Movie updateMovie(Long movieID, String movieName, String movieCategory,
                      String movieDescription, String movieLanguage, MovieValidationService movieValidationService);

    List<Movie> getMoviesByCategory(String movieCategory);

    Movie getMovieByName(String movieName) throws MovieNotFoundException;
}
