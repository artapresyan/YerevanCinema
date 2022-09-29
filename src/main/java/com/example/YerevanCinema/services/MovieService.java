package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie getMovieByID(Long movieID) throws MovieNotFoundException {
        Optional<Movie> movie = movieRepository.findById(movieID);
        if (movie.isPresent()){
            return movie.get();
        }else
            throw new MovieNotFoundException(String.format("No movie found with %s id",movieID));
    }


}
