package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Movie;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.MovieSessionNotFoundException;
import com.example.YerevanCinema.repositories.MovieSessionRepository;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class MovieSessionServiceImplTest {
    @Mock
    private MovieSessionRepository movieSessionRepository;

    @InjectMocks
    private MovieSessionServiceImpl movieSessionService;

    @InjectMocks
    private MovieSessionValidationService movieSessionValidationService;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;
    private static final List<MovieSession> MOVIE_SESSIONS = new ArrayList<>();

    @Test
    public void getMovieSessionByIDTest() throws MovieSessionNotFoundException {
        MovieSession expectedMovieSession = new MovieSession();
        expectedMovieSession.setMovieSessionID(12L);
        expectedMovieSession.setMovieSessionStart(LocalDateTime.now().toString());
        expectedMovieSession.setMovieSessionEnd(LocalDateTime.now().plusHours(2).toString());
        Movie movie = new Movie();
        movie.setMovieID(10L);
        expectedMovieSession.setMovie(movie);

        when(movieSessionRepository.findById(expectedMovieSession.getMovieSessionID()))
                .thenReturn(Optional.of(expectedMovieSession));

        MovieSession actualMovieSession = movieSessionService.getMovieSessionByID(expectedMovieSession.getMovieSessionID());

        assertEquals(expectedMovieSession, actualMovieSession);
    }

    @Test
    public void getAllMovieSessionsTest() {
        fillMovieSessions();

        when(movieSessionRepository.findAll()).thenReturn(MOVIE_SESSIONS);

        List<MovieSession> actualMovieSessions = movieSessionService.getAllMovieSessions();

        assertTrue(actualMovieSessions.containsAll(MOVIE_SESSIONS));

        MOVIE_SESSIONS.clear();
    }

    @Test
    public void addMovieSessionTest() {
        fillMovieSessions();
        String decodedPassword = "ArtBab1234&";
        MovieSession expectedMovieSession = new MovieSession(LocalDateTime.now().plusDays(28).toString(),
                LocalDateTime.now().plusDays(28).plusHours(3).toString(), 2500, new Hall("Red",
                100), new Movie("Avatar", "Fantasy", null,
                "Armenian"), new Admin("Artak", "Babyan",
                "artak.babyan@gmail.com", "artbabyan", passwordEncoder.encode(decodedPassword)));

        expectedMovieSession.getHall().setMovieSessions(new HashSet<>(MOVIE_SESSIONS));

        when(movieSessionRepository.save(any(MovieSession.class))).thenReturn(expectedMovieSession);

        MovieSession actualMovieSession = movieSessionService.addMovieSession(expectedMovieSession.getMovieSessionStart(),
                expectedMovieSession.getMovieSessionEnd(), expectedMovieSession.getMovieSessionPrice(),
                expectedMovieSession.getHall(), expectedMovieSession.getMovie(), expectedMovieSession.getAdmin(),
                decodedPassword, passwordEncoder, movieSessionValidationService);

        assertNotNull(actualMovieSession);
        MOVIE_SESSIONS.clear();
    }

    @Test
    public void removeMovieSessionTest() {
        fillMovieSessions();

        List<MovieSession> actualMovieSessions = MOVIE_SESSIONS.stream()
                .map(expectedMovieSession -> {
                    Long id = expectedMovieSession.getMovieSessionID();

                    when(movieSessionRepository.findById(id)).thenReturn(Optional.of(expectedMovieSession));

                    Admin admin = expectedMovieSession.getAdmin();
                    String decodedPassword = admin.getAdminPassword();
                    admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));

                    MovieSession actualMovieSession = movieSessionService.removeMovieSession(admin,
                            decodedPassword, expectedMovieSession.getMovieSessionID(), passwordEncoder);

                    verify(movieSessionRepository).deleteById(id);

                    assertEquals(expectedMovieSession, actualMovieSession);

                    return actualMovieSession;
                }).collect(Collectors.toList());

        assertTrue(actualMovieSessions.containsAll(MOVIE_SESSIONS));

        MOVIE_SESSIONS.clear();
    }

    @Test
    public void updateMovieSessionTest() {
        fillMovieSessions();

        String decodedPassword = "ArtBab1234&";
        MovieSession movieSession = new MovieSession(LocalDateTime.now().plusDays(28).toString(),
                LocalDateTime.now().plusDays(28).plusHours(3).toString(), 2500, new Hall("Red",
                100), new Movie("Avatar", "Fantasy", null,
                "Armenian"), new Admin("Artak", "Babyan",
                "artak.babyan@gmail.com", "artbabyan", passwordEncoder.encode(decodedPassword)));
        movieSession.setMovieSessionID(19L);
        movieSession.getHall().setMovieSessions(new HashSet<>(MOVIE_SESSIONS));

        MovieSession expectedMovieSession = new MovieSession(LocalDateTime.now().plusDays(28).toString(),
                LocalDateTime.now().plusDays(28).plusHours(3).toString(), 3500, new Hall("Blue",
                150), new Movie("Avatar", "Fantasy", null,
                "English"), new Admin("Artak", "Babyan",
                "artak.babyan@gmail.com", "artbabyan", passwordEncoder.encode(decodedPassword)));
        expectedMovieSession.setMovieSessionID(19L);
        expectedMovieSession.getHall().setMovieSessions(new HashSet<>(MOVIE_SESSIONS));

        when(movieSessionRepository.save(any(MovieSession.class))).thenReturn(expectedMovieSession);
        when(movieSessionRepository.findById(19L)).thenReturn(Optional.of(movieSession));

        MovieSession actualMovieSession = movieSessionService.updateMovieSession(movieSession.getMovieSessionID(),
                expectedMovieSession.getMovieSessionStart(), expectedMovieSession.getMovieSessionEnd(),
                expectedMovieSession.getMovieSessionPrice(), expectedMovieSession.getHall(), expectedMovieSession.getMovie(),
                expectedMovieSession.getAdmin(), movieSessionValidationService);

        assertTrue(expectedMovieSession.toString().equals(actualMovieSession.toString()));
    }

    @Test
    public void getAllMovieSessionsByStartTest() {
        fillMovieSessions();

        LocalDateTime start = LocalDateTime.now();
        List<MovieSession> expectedMovieSessions = MOVIE_SESSIONS.stream()
                .filter(movieSession -> movieSession.getHall().getHallCapacity() > 170)
                .peek(movieSession -> movieSession.setMovieSessionStart(start.toString())).collect(Collectors.toList());

        when(movieSessionRepository.getByMovieSessionStart(start.toString())).thenReturn(expectedMovieSessions);

        List<MovieSession> actualMovieSessions = movieSessionService.getAllMovieSessionsByStart(start.toString());

        assertTrue(actualMovieSessions.containsAll(expectedMovieSessions));

        MOVIE_SESSIONS.clear();
    }

    @Test
    public void getAllMovieSessionsByPriceTest() {
        fillMovieSessions();

        List<MovieSession> expectedMovieSessions = MOVIE_SESSIONS.stream()
                .filter(movieSession -> movieSession.getHall().getHallCapacity() > 170)
                .peek(movieSession -> movieSession.setMovieSessionPrice(3000)).collect(Collectors.toList());

        when(movieSessionRepository.getByMovieSessionPrice(3000)).thenReturn(expectedMovieSessions);

        List<MovieSession> actualMovieSessions = movieSessionService.getAllMovieSessionsByPrice(3000);

        assertTrue(actualMovieSessions.containsAll(expectedMovieSessions));

        MOVIE_SESSIONS.clear();
    }

    private void fillMovieSessions() {
        for (int i = 1, suffix = 'a'; i < 21; i++, suffix++) {
            MovieSession movieSession = new MovieSession(LocalDateTime.now().plusHours(i).toString(),
                    LocalDateTime.now().plusHours(i + 2).toString(), 2000 + 150 * i,
                    new Hall("HallName" + suffix, 60 + 10 * i),
                    new Movie("MovieName" + suffix, "MovieCategory" + suffix, null,
                            "MovieLanguage" + suffix), new Admin("AdminName" + suffix,
                    "AdminSurname" + suffix, "AdminEmail" + suffix,
                    "AdminUsername" + suffix, "Password/1" + suffix));
            movieSession.setMovieSessionID((long) i);
            MOVIE_SESSIONS.add(movieSession);
        }
    }
}
