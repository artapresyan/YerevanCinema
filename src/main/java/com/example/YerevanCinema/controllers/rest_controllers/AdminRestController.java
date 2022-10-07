package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.*;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("rest/api/admin/")
public class AdminRestController {

    private final GmailClientServiceImpl gmailClientService;
    private final AdminServiceImpl adminService;
    private final TicketServiceImpl ticketService;
    private final MovieSessionServiceImpl movieSessionService;
    private final MovieServiceImpl movieService;
    private final HallServiceImpl hallService;
    private final CustomerServiceImpl customerService;
    private final PasswordEncoder passwordEncoder;
    private final MovieValidationService movieValidationService;
    private final MovieSessionValidationService movieSessionValidationService;
    private final AdminValidationService userValidationService;

    public AdminRestController(GmailClientServiceImpl gmailClientService, AdminServiceImpl adminService,
                               TicketServiceImpl ticketService, MovieSessionServiceImpl movieSessionService,
                               MovieServiceImpl movieService, HallServiceImpl hallService,
                               CustomerServiceImpl customerService, PasswordEncoder passwordEncoder,
                               MovieValidationService movieValidationService,
                               MovieSessionValidationService movieSessionValidationService,
                               AdminValidationService userValidationService) {
        this.gmailClientService = gmailClientService;
        this.adminService = adminService;
        this.ticketService = ticketService;
        this.movieSessionService = movieSessionService;
        this.movieService = movieService;
        this.hallService = hallService;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.movieValidationService = movieValidationService;
        this.movieSessionValidationService = movieSessionValidationService;
        this.userValidationService = userValidationService;
    }

    @GetMapping
    public Admin getAdminMainPage(HttpSession session) {
        return (Admin) session.getAttribute("user");
    }

    @GetMapping("about")
    public Admin getAdminAboutPage(HttpSession session) {
        return (Admin) session.getAttribute("user");
    }

    @GetMapping("contact")
    public Admin getAdminContactPage(HttpSession session) {
        return (Admin) session.getAttribute("user");
    }

    @PostMapping("contact")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Admin admin = (Admin) session.getAttribute("user");
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(admin.getAdminEmail(), message);
            return mimeMessage.getContent().toString();
        } catch (IOException | MessagingException e) {
            return e.getMessage();
        }
    }

    @GetMapping("details")
    public Admin getAccountDetailsPage(HttpSession session) {
        return (Admin) session.getAttribute("user");
    }

    @GetMapping("details/edit")
    public Admin getAccountDetailsEditPage(HttpSession session) {
        return (Admin) session.getAttribute("user");
    }

    @PutMapping("details/edit")
    public Admin updateAccountDetails(@RequestParam(value = "name", required = false) String newName,
                                      @RequestParam(value = "surname", required = false) String newSurname,
                                      @RequestParam(value = "username", required = false) String newUsername,
                                      @RequestParam(value = "email", required = false) String newEmail,
                                      @RequestParam(value = "password", required = false) String newPassword,
                                      HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        adminService.updateAdminData(admin.getAdminId(), newName, newSurname, newUsername,
                newEmail, admin.getAdminPassword(), newPassword, userValidationService, passwordEncoder);
        try {
            admin = adminService.getAdminByID(admin.getAdminId());
            session.setAttribute("user", admin);
            return admin;
        } catch (UserNotFoundException e) {
            return null;
        }
    }

    @GetMapping("tickets")
    public List<Ticket> getAllPurchasedTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("sessions")
    public List<MovieSession> getSessionsPage() {
        return movieSessionService.getAllMovieSessions().stream().filter(movieSession ->
                        movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(14)))
                .collect(Collectors.toList());
    }

    @GetMapping("sessions/all")
    public List<MovieSession> getAllSessions() {
        return movieSessionService.getAllMovieSessions();
    }

    @PutMapping("sessions/all")
    public MovieSession changeSessionDetails(@RequestParam("movieSessionID") Long movieSessionID,
                                             @RequestParam(value = "movieSessionStart", required = false) LocalDateTime movieSessionStart,
                                             @RequestParam(value = "movieSessionEnd", required = false) LocalDateTime movieSessionEnd,
                                             @RequestParam(value = "movieSessionPrice", required = false) Integer movieSessionPrice,
                                             @RequestParam(value = "movieSessionHall", required = false) Long movieSessionHallID,
                                             @RequestParam(value = "movieSessionMovie", required = false) Long movieSessionMovieID,
                                             HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        Hall hall = null;
        Movie movie = null;
        try {
            hall = hallService.getHallByID(movieSessionHallID);
        } catch (HallNotFoundException ignored) {
        }
        try {
            movie = movieService.getMovieByID(movieSessionMovieID);
        } catch (MovieNotFoundException ignored) {
        }
        return movieSessionService.updateMovieSession(movieSessionID, movieSessionStart, movieSessionEnd, movieSessionPrice,
                hall, movie, admin, movieSessionValidationService);
    }

    @PostMapping("sessions/all")
    public MovieSession addSession(@RequestParam("movieSessionStart") LocalDateTime movieSessionStart,
                                   @RequestParam("movieSessionEnd") LocalDateTime movieSessionEnd,
                                   @RequestParam("movieSessionPrice") Integer movieSessionPrice,
                                   @RequestParam("movieSessionHall") Long movieSessionHallID,
                                   @RequestParam("movieSessionMovie") Long movieSessionMovieID,
                                   @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        Hall hall = null;
        Movie movie = null;
        try {
            hall = hallService.getHallByID(movieSessionHallID);
        } catch (HallNotFoundException ignored) {
        }
        try {
            movie = movieService.getMovieByID(movieSessionMovieID);
        } catch (MovieNotFoundException ignored) {
        }
        return movieSessionService.addMovieSession(movieSessionStart, movieSessionEnd, movieSessionPrice, hall, movie, admin,
                password, passwordEncoder, movieSessionValidationService);
    }

    @DeleteMapping("sessions/all")
    public MovieSession removeSession(@RequestParam("movieSessionID") Long movieSessionID,
                                      @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        return movieSessionService.removeMovieSession(admin, password, movieSessionID, passwordEncoder);
    }

    @GetMapping("sessions/movie")
    public List<MovieSession> getSessionsByMovieName(@RequestParam("movie_name") String name) {
        return movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieName().equals(name))
                .collect(Collectors.toList());
    }

    @GetMapping("sessions/category")
    public List<MovieSession> getSessionsByMovieCategory(@RequestParam("movie_category") String movieCategory) {
        return movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(movieCategory))
                .collect(Collectors.toList());
    }

    @GetMapping("sessions/start")
    public List<MovieSession> getSessionsByStart(@RequestParam("movie_start") LocalDateTime movieStart) {
        return movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().isEqual(movieStart))
                .collect(Collectors.toList());
    }

    @GetMapping("sessions/hall")
    public List<MovieSession> getSessionsByHall(@RequestParam("movie_hall") Long hallID) {
        return movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getHall().getHallID().equals(hallID))
                .collect(Collectors.toList());
    }

    @GetMapping("customers/all")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @DeleteMapping("customers/all")
    public Customer removeCustomer(@RequestParam("customerID") Long customerID,
                                   @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        return customerService.adminRemoveCustomer(customerID, admin, password, passwordEncoder);
    }

    @GetMapping("movies/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PutMapping("movies/all")
    public Movie updateMovieDetails(@RequestParam("movieID") Long movieID,
                                    @RequestParam(value = "movieName", required = false) String movieName,
                                    @RequestParam(value = "movieCategory", required = false) String movieCategory,
                                    @RequestParam(value = "movieDescription", required = false) String movieDescription,
                                    @RequestParam(value = "movieLanguage", required = false) String movieLanguage) {
        Movie movie;
        try {
            movie = movieService.getMovieByID(movieID);
        } catch (MovieNotFoundException e) {
            return null;
        }
        return movieService.updateMovie(movie.getMovieID(), movieName, movieCategory, movieDescription, movieLanguage,
                movieValidationService);
    }

    @PostMapping("movies/all")
    public Movie addMovie(HttpSession session, @RequestParam("password") String password,
                          @RequestParam("movieName") String movieName, @RequestParam("movieCategory") String movieCategory,
                          @RequestParam(value = "movieDescription", required = false) String movieDescription,
                          @RequestParam(value = "movieLanguage", required = false) String movieLanguage) {
        Admin admin = (Admin) session.getAttribute("user");
        return movieService.addMovie(admin.getAdminId(), password, movieName, movieCategory, movieDescription,
                movieLanguage, adminService, passwordEncoder, movieValidationService);
    }

    @DeleteMapping("movies/all")
    public Movie removeMovie(@RequestParam("movieID") Long movieID, @RequestParam("password") String password,
                             HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        return movieService.removeMovie(movieID, admin.getAdminId(), password, adminService, passwordEncoder);
    }
}
