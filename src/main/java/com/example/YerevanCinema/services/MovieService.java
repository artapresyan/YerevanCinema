package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;

import java.util.List;

public interface MovieService {

    Movie getMovieByID(Long movieID) throws MovieNotFoundException;

    List<Movie> getAllMovies();

    Movie addMovie(Long adminID, String password, String movieName, String movieCategory,
                   String movieDescription, String movieLanguage);

    Movie removeMovie(Long movieID, Long adminID, String password);

    Movie updateMovie(Long movieID, String movieName, String movieCategory,
                      String movieDescription, String movieLanguage);

    List<Movie> getMoviesByCategory(String movieCategory);

    Movie getMovieByName(String movieName) throws MovieNotFoundException;
}
