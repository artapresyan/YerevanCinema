package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.*;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import com.example.YerevanCinema.services.validations.HallValidationService;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rest/api/admin/")
public class AdminRestController {

    private final AdminServiceImpl adminService;
    private final TicketServiceImpl ticketService;
    private final MovieSessionServiceImpl movieSessionService;
    private final MovieServiceImpl movieService;
    private final HallServiceImpl hallService;
    private final CustomerServiceImpl customerService;
    private final MovieValidationService movieValidationService;
    private final GmailClientServiceImpl gmailClientService;
    private final MovieSessionValidationService movieSessionValidationService;
    private final HallValidationService hallValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AdminValidationService userValidationService;

    public AdminRestController(AdminServiceImpl adminService, TicketServiceImpl ticketService,
                               MovieSessionServiceImpl movieSessionService, MovieServiceImpl movieService,
                               HallServiceImpl hallService, CustomerServiceImpl customerService,
                               MovieValidationService movieValidationService, GmailClientServiceImpl gmailClientService,
                               PasswordEncoder passwordEncoder, MovieSessionValidationService movieSessionValidationService,
                               HallValidationService hallValidationService, AdminValidationService userValidationService) {
        this.adminService = adminService;
        this.ticketService = ticketService;
        this.movieSessionService = movieSessionService;
        this.movieService = movieService;
        this.hallService = hallService;
        this.customerService = customerService;
        this.movieValidationService = movieValidationService;
        this.gmailClientService = gmailClientService;
        this.movieSessionValidationService = movieSessionValidationService;
        this.hallValidationService = hallValidationService;
        this.passwordEncoder = passwordEncoder;
        this.userValidationService = userValidationService;
    }

    @GetMapping
    public ResponseEntity<Admin> getAdminMainPage(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("about")
    public ResponseEntity<Admin> getAdminAboutPage(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("contact")
    public ResponseEntity<Admin> getAdminContactPage(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("contact_post")
    public ResponseEntity<String> sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Admin admin = (Admin) session.getAttribute("admin");
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(admin.getAdminEmail(), message);
            return ResponseEntity.ok(mimeMessage.getContent().toString());
        } catch (MessagingException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("details")
    public ResponseEntity<Admin> getAccountDetailsPage(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("details/edit")
    public ResponseEntity<Admin> getAccountDetailsEditPage(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("details/edit_put")
    public ResponseEntity<Admin> editAccountDetails(@RequestParam(value = "admin_name", required = false) String newName,
                                                    @RequestParam(value = "admin_surname", required = false) String newSurname,
                                                    @RequestParam(value = "admin_username", required = false) String newUsername,
                                                    @RequestParam(value = "admin_email", required = false) String newEmail,
                                                    @RequestParam(value = "admin_new_password", required = false) String newPassword,
                                                    @RequestParam(value = "admin_new_password_confirm", required = false) String confirmNewPassword,
                                                    @RequestParam("admin_password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (newPassword.equals(confirmNewPassword))
            adminService.updateAdminData(admin.getAdminId(), newName, newSurname, newUsername,
                    newEmail, password, newPassword, userValidationService, passwordEncoder);
        try {
            admin = adminService.getAdminByID(admin.getAdminId());
            session.setAttribute("admin", admin);
            return ResponseEntity.ok(admin);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("tickets/all")
    public ResponseEntity<List<Ticket>> getAllPurchasedTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("sessions")
    public ResponseEntity<List<MovieSession>> getSessionsPage() {
        return ResponseEntity.ok(movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(14))).collect(Collectors.toList()));
    }

    @GetMapping("sessions/all")
    public ResponseEntity<List<MovieSession>> getAllSessions() {
        return ResponseEntity.ok(movieSessionService.getAllMovieSessions());
    }

    @PostMapping("sessions/all")
    public ResponseEntity<MovieSession> changeSessionDetails(@RequestParam("movieSessionID") Long movieSessionID,
                                                             @RequestParam(value = "movieSessionStart", required = false) String movieSessionStart,
                                                             @RequestParam(value = "movieSessionEnd", required = false) String movieSessionEnd,
                                                             @RequestParam(value = "movieSessionPrice", required = false) Integer movieSessionPrice,
                                                             @RequestParam(value = "movieSessionHall", required = false) Long movieSessionHallID,
                                                             @RequestParam(value = "movieSessionMovie", required = false) Long movieSessionMovieID,
                                                             HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
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
        MovieSession movieSession = movieSessionService.updateMovieSession(movieSessionID, movieSessionStart, movieSessionEnd,
                movieSessionPrice, hall, movie, admin, movieSessionValidationService);
        if (movieSession != null) {
            return ResponseEntity.ok(movieSession);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("sessions/all_add")
    public ResponseEntity<MovieSession> addSession(@RequestParam("movie_name") String movieName,
                                                   @RequestParam("session_start") String sessionStart,
                                                   @RequestParam("session_end") String sessionEnd,
                                                   @RequestParam("session_hall") String sessionHallName,
                                                   @RequestParam("session_price") Integer sessionPrice,
                                                   @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        Hall hall = null;
        Movie movie = null;
        try {
            hall = hallService.getHallByName(sessionHallName);
        } catch (HallNotFoundException ignored) {
        }
        try {
            movie = movieService.getMovieByName(movieName);
        } catch (MovieNotFoundException ignored) {
        }
        MovieSession movieSession = movieSessionService.addMovieSession(sessionStart, sessionEnd, sessionPrice, hall, movie, admin, password,
                passwordEncoder, movieSessionValidationService);
        if (movieSession != null) {
            return ResponseEntity.ok(movieSession);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("sessions/all_remove")
    public ResponseEntity<MovieSession> removeSession(@RequestParam("session_id") Long movieSessionID,
                                                      @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        MovieSession movieSession = movieSessionService.removeMovieSession(admin, password, movieSessionID, passwordEncoder);
        if (movieSession != null) {
            return ResponseEntity.ok(movieSession);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("sessions/selected")
    public ResponseEntity<List<MovieSession>> getSessionsByMovieName(@RequestParam("key_value") String keyValue,
                                                                     @RequestParam("selected") String selected) {
        try {
            return ResponseEntity.ok(getSelectedMovieSessions(keyValue, selected));
        } catch (MovieNotFoundException | HallNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("customers/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping("customers/all_remove")
    public ResponseEntity<Customer> removeCustomer(@RequestParam(value = "customer_id") Long customerID,
                                                   @RequestParam(value = "password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        Customer customer = customerService.adminRemoveCustomer(customerID, admin, password, passwordEncoder);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("movies/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping("movies/all_update")
    public ResponseEntity<Movie> updateMovieDetails(@RequestParam("movieID") Long movieID,
                                                    @RequestParam(value = "movieName", required = false) String movieName,
                                                    @RequestParam(value = "movieCategory", required = false) String movieCategory,
                                                    @RequestParam(value = "movieDescription", required = false) String movieDescription,
                                                    @RequestParam(value = "movieLanguage", required = false) String movieLanguage) {
        Movie movie;
        try {
            movie = movieService.getMovieByID(movieID);
        } catch (MovieNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
        movie = movieService.updateMovie(movie.getMovieID(), movieName, movieCategory, movieDescription, movieLanguage,
                movieValidationService);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("movies/all_add")
    public ResponseEntity<Movie> addMovie(@RequestParam("password") String password,
                                          @RequestParam("movie_name") String movieName, @RequestParam("movie_category") String movieCategory,
                                          @RequestParam(value = "movie_description", required = false) String movieDescription,
                                          @RequestParam(value = "movie_language", required = false) String movieLanguage,
                                          HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        Movie movie = movieService.addMovie(admin.getAdminId(), password, movieName, movieCategory, movieDescription, movieLanguage,
                adminService, passwordEncoder, movieValidationService);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("movies/all_remove")
    public ResponseEntity<Movie> removeMovie(@RequestParam("movie_id") Long movieID, @RequestParam("password") String password,
                                             HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        Movie movie = movieService.removeMovie(movieID, admin.getAdminId(), password, adminService, passwordEncoder);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("halls/all")
    public ResponseEntity<List<Hall>> getHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @PostMapping("halls/all_add")
    public ResponseEntity<Hall> addHall(@RequestParam("hall_name") String hallName,
                                        @RequestParam("hall_capacity") Integer hallCapacity,
                                        @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        Hall hall = hallService.addHall(admin.getAdminId(), password, hallName, hallCapacity, hallValidationService,
                adminService, passwordEncoder);
        if (hall != null) {
            return ResponseEntity.ok(hall);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private List<MovieSession> getSelectedMovieSessions(String keyValue, String selected) throws
            MovieNotFoundException, HallNotFoundException {
        if (keyValue.equalsIgnoreCase("Movie"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovie().getMovieName().equals(selected))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Category"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(selected))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Price"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovieSessionPrice().equals(Integer.parseInt(selected)))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Hall"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getHall().getHallName().equals(selected))
                    .collect(Collectors.toList());
        else
            return List.of();
    }

}
