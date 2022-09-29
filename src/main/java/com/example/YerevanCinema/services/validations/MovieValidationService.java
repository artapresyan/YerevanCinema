package com.example.YerevanCinema.services.validations;

import com.example.YerevanCinema.repositories.MovieRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MovieValidationService {

    private final MovieRepository movieRepository;
    private final Logger logger = LogManager.getLogger();

    public MovieValidationService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void validateMovieName(String movieName) throws IOException {
        if (movieName == null || movieName.length() == 0) {
            logger.log(Level.FATAL, String.format("Please be aware, ' %s ' is invalid movie name", movieName));
            throw new IOException();
        }else if (movieRepository.getByMovieName(movieName) != null){
            logger.log(Level.FATAL, String.format("Please be aware, movie ' %s ' already exists", movieName));
            throw new IOException();
        }
    }

    public void validateMovieCategory(String movieCategory) throws IOException {
        if (movieCategory == null || movieCategory.length() == 0 || !movieCategory.matches("\\b+")) {
            logger.log(Level.FATAL, String.format("Please be aware, ' %s ' is invalid movie category", movieCategory));
            throw new IOException();
        }
    }

    public void validateMovieDescription(String movieDescription) throws IOException {
        if (movieDescription != null && movieDescription.length() != 0
                && !movieDescription.matches("(?=.{40,}[a-zA-Z])[.]+")) {
            logger.log(Level.FATAL, "Please be aware, description must contain minimum 40 alphabetic characters");
            throw new IOException();
        }
    }

    public void validateMovieLanguage(String movieLanguage) throws IOException{
        if (movieLanguage != null && movieLanguage.length() != 0 && !movieLanguage.matches("\\b+")) {
            logger.log(Level.FATAL, String.format("Please be aware, ' %s ' is invalid language", movieLanguage));
            throw new IOException();
        }
    }
}
