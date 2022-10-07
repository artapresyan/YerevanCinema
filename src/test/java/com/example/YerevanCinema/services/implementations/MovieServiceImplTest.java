package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.repositories.MovieRepository;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @InjectMocks
    private MovieValidationService movieValidationService;

    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getMovieByIDTest() throws MovieNotFoundException {
        Movie expectedMovie = new Movie();
        expectedMovie.setMovieID(43L);
        expectedMovie.setMovieName("James Bond");

        when(movieRepository.findById(expectedMovie.getMovieID())).thenReturn(Optional.of(expectedMovie));

        Movie actualMovie = movieService.getMovieByID(expectedMovie.getMovieID());

        assertEquals(expectedMovie, actualMovie);
    }

    @Test
    public void getAllMoviesTest() {
        List<Movie> expectedMovies = getTwentyMovies();

        when(movieRepository.findAll()).thenReturn(expectedMovies);

        List<Movie> actualMovies = movieService.getAllMovies();

        assertTrue(actualMovies.containsAll(expectedMovies));
    }

    @Test
    public void addMovieTest() {

        Movie expectedMovie = new Movie("James Bond", "Action", null, "English");
        String decodedPassword = "Password23/";
        Admin admin = new Admin();
        admin.setAdminId(1L);
        admin.setAdminUsername("username");
        admin.setAdminPassword(passwordEncoder.encode(decodedPassword));

        when(adminRepository.findById(admin.getAdminId())).thenReturn(Optional.of(admin));
        when(movieRepository.save(any(Movie.class))).thenReturn(expectedMovie);

        Movie actualMovie = movieService.addMovie(admin.getAdminId(), decodedPassword, expectedMovie.getMovieName(),
                expectedMovie.getMovieCategory(), expectedMovie.getMovieDescription(), expectedMovie.getMovieLanguage(),
                adminService, passwordEncoder, movieValidationService);
        assertEquals(expectedMovie.getMovieName(), actualMovie.getMovieName());
        assertEquals(expectedMovie.getMovieCategory(), actualMovie.getMovieCategory());
        assertEquals(expectedMovie.getMovieLanguage(), actualMovie.getMovieLanguage());
    }

    @Test
    public void removeMovieTest() {
        Movie expectedMovie = new Movie("James Bond", "Action", null, "English");
        expectedMovie.setMovieID(4L);
        String decodedPassword = "Password23/";
        Admin admin = new Admin();
        admin.setAdminId(1L);
        admin.setAdminUsername("username");
        admin.setAdminPassword(passwordEncoder.encode(decodedPassword));

        when(adminRepository.findById(admin.getAdminId())).thenReturn(Optional.of(admin));
        when(movieRepository.findById(expectedMovie.getMovieID())).thenReturn(Optional.of(expectedMovie));

        Movie actualMovie = movieService.removeMovie(expectedMovie.getMovieID(), admin.getAdminId(), decodedPassword,
                adminService, passwordEncoder);

        verify(movieRepository).deleteById(expectedMovie.getMovieID());

        assertEquals(expectedMovie.getMovieName(), actualMovie.getMovieName());
        assertEquals(expectedMovie.getMovieCategory(), actualMovie.getMovieCategory());
        assertEquals(expectedMovie.getMovieLanguage(), actualMovie.getMovieLanguage());
    }

    @Test
    public void updateMovieTest() {
        Movie movie = new Movie("James Bond", "Action", null, "English");
        movie.setMovieID(3L);
        Movie expectedMovie = new Movie("James Bond", "Action",
                "Movie description Movie description Movie description Movie description Movie description " +
                        "Movie description Movie description Movie description Movie description Movie description",
                "Russian");
        expectedMovie.setMovieID(3L);

        when(movieRepository.findById(movie.getMovieID())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(expectedMovie);
        Movie actualMovie = movieService.updateMovie(movie.getMovieID(), movie.getMovieName(),
                movie.getMovieCategory(), expectedMovie.getMovieDescription(), expectedMovie.getMovieLanguage(),
                movieValidationService);
        assertEquals(expectedMovie.getMovieID(), actualMovie.getMovieID());
        assertEquals(expectedMovie.getMovieDescription(), actualMovie.getMovieDescription());
        assertEquals(expectedMovie.getMovieLanguage(), actualMovie.getMovieLanguage());
    }

    @Test
    public void getMoviesByCategoryTest() {
        List<Movie> movies = getTwentyMovies();

        for (int i = 2; i < movies.size(); i += 3) {
            movies.get(i).setMovieCategory("Horror");
        }
        List<Movie> expectedMovies = movies.stream()
                .filter(movie -> movie.getMovieCategory().equals("Horror")).collect(Collectors.toList());

        when(movieRepository.getByMovieCategory("Horror")).thenReturn(expectedMovies);

        List<Movie> actualMovies = movieService.getMoviesByCategory("Horror");

        assertTrue(actualMovies.containsAll(expectedMovies));
    }


    @Test
    public void getMovieByNameTest() throws MovieNotFoundException {
        Movie expectedMovie = new Movie("James Bond", "Action", null, "English");

        when(movieRepository.getByMovieName("James Bond")).thenReturn(expectedMovie);

        Movie actualMovie = movieService.getMovieByName("James Bond");

        assertEquals(expectedMovie, actualMovie);
    }

    private List<Movie> getTwentyMovies() {
        List<Movie> movies = new ArrayList<>();
        String movieName = "Movie_Name_";
        String movieCategory = "Movie_Category_";
        String movieDescription = "Movie_Description_";
        String movieLanguage = "Movie_Language_";

        for (int i = 1, suffix = 'a'; i < 21; i++, suffix++) {
            Movie movie = new Movie(movieName + suffix, movieCategory + suffix,
                    movieDescription + suffix, movieLanguage + suffix);
            movie.setMovieID((long) i);
            movies.add(movie);
        }
        return movies;
    }
}
