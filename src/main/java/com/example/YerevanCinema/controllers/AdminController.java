package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.*;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/")
public class AdminController {

    private final GmailClientServiceImpl gmailClientService;
    private final AdminServiceImpl adminService;
    private final TicketServiceImpl ticketService;
    private final MovieSessionServiceImpl movieSessionService;
    private final MovieServiceImpl movieService;
    private final HallServiceImpl hallService;
    private final CustomerServiceImpl customerService;

    public AdminController(GmailClientServiceImpl gmailClientService, AdminServiceImpl adminService,
                           TicketServiceImpl ticketService, MovieSessionServiceImpl movieSessionService,
                           MovieServiceImpl movieService, HallServiceImpl hallService, CustomerServiceImpl customerService) {
        this.gmailClientService = gmailClientService;
        this.adminService = adminService;
        this.ticketService = ticketService;
        this.movieSessionService = movieSessionService;
        this.movieService = movieService;
        this.hallService = hallService;
        this.customerService = customerService;
    }

    @GetMapping
    public String getAdminMainPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        return "admin_main_view";
    }

    @GetMapping("about")
    public String getAdminAboutPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        return "admin_about_view";
    }

    @GetMapping("contact")
    public String getAdminContactPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        return "admin_contact_view";
    }

    @PostMapping("contact_post")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Admin admin = (Admin) session.getAttribute("user");
            gmailClientService.getSimpleMessage(admin.getAdminEmail(), message);
        } catch (MessagingException e) {
            return "admin_contact_view";
        }
        return "admin_main_view";
    }

    @GetMapping("details")
    public String getAccountDetailsPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        return "admin_details_view";
    }

    @GetMapping("details/edit")
    public String getAccountDetailsEditPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        return "admin_details_edit_view";
    }

    @PutMapping("details/edit_put")
    public String updateAccountDetails(@RequestParam(value = "admin_name", required = false) String newName,
                                       @RequestParam(value = "admin_surname", required = false) String newSurname,
                                       @RequestParam(value = "admin_username", required = false) String newUsername,
                                       @RequestParam(value = "admin_email", required = false) String newEmail,
                                       @RequestParam(value = "admin_new_password", required = false) String newPassword,
                                       HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        adminService.updateAdminData(admin.getAdminId(), newName, newSurname, newUsername,
                newEmail, admin.getAdminPassword(), newPassword);
        try {
            admin = adminService.getAdminByID(admin.getAdminId());
            session.setAttribute("user", admin);
            model.addAttribute("user", admin);
            return "admin_details_view";
        } catch (UserNotFoundException e) {
            return "admin_details_edit_view";
        }
    }

    @GetMapping("tickets/all")
    public String getAllPurchasedTickets(Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();
        model.addAttribute("tickets", tickets);
        return "admin_tickets_all_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage(Model model) {
        List<MovieSession> sessions = movieSessionService.getAllMovieSessions().stream().filter(movieSession ->
                        movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(14)))
                .collect(Collectors.toList());
        model.addAttribute("sessions", sessions);
        return "admin_sessions_view";
    }

    @GetMapping("sessions/all")
    public String getAllSessions(Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions();
        model.addAttribute("movie_sessions_all", movieSessions);
        return "admin_sessions_all_view";
    }

    @PutMapping("sessions/all")
    public String changeSessionDetails(@RequestParam("movieSessionID") Long movieSessionID,
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
        movieSessionService.updateMovieSession(movieSessionID, movieSessionStart, movieSessionEnd, movieSessionPrice,
                hall, movie, admin);
        return "admin_sessions_all_view";
    }

    @PostMapping("sessions/all_add")
    public String addSession(@RequestParam("movie_name") String movieName,
                             @RequestParam("session_start") LocalDateTime sessionStart,
                             @RequestParam("session_end") LocalDateTime sessionEnd,
                             @RequestParam("session_hall") String sessionHallName,
                             @RequestParam("session_price") Integer sessionPrice,
                             @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
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
        movieSessionService.addMovieSession(sessionStart, sessionEnd, sessionPrice, hall, movie, admin, password);
        return "admin_sessions_all_view";
    }

    @DeleteMapping("sessions/all_remove")
    public String removeSession(@RequestParam("movie_name") String movieName,
                                @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        try {
            Movie movie = movieService.getMovieByName(movieName);
            movieSessionService.removeMovieSession(admin, password, movie.getMovieID());
        } catch (MovieNotFoundException ignored) {
        }
        return "admin_sessions_all_view";
    }

    @GetMapping("sessions/movie")
    public String getSessionsByMovieName(@RequestParam("movie_name") String name, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieName().equals(name))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "admin_sessions_selected_view";
    }

    @GetMapping("sessions/category")
    public String getSessionsByMovieCategory(@RequestParam("movie_category") String movieCategory, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(movieCategory))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "admin_sessions_selected_view";
    }

    @GetMapping("sessions/start")
    public String getSessionsByStart(@RequestParam("movie_start") LocalDateTime movieStart, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().isEqual(movieStart))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "admin_sessions_selected_view";
    }

    @GetMapping("sessions/hall")
    public String getSessionsByHall(@RequestParam("movie_hall") Long hallID, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getHall().getHallID().equals(hallID))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "admin_sessions_selected_view";
    }

    @GetMapping("customers/all")
    public String getAllCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "admin_customers_all_view";
    }

    @DeleteMapping("customers/all_remove")
    public String removeCustomer(@RequestParam("customer_id") Long customerID,
                                 @RequestParam("password") String password, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        customerService.adminRemoveCustomer(customerID, admin, password);
        return "admin_customers_all_view";
    }

    @GetMapping("movies/all")
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("allMovies", movies);
        return "admin_movies_all_view";
    }

    @PutMapping("movies/all_update")
    public String updateMovieDetails(@RequestParam("movieID") Long movieID,
                                     @RequestParam(value = "movieName", required = false) String movieName,
                                     @RequestParam(value = "movieCategory", required = false) String movieCategory,
                                     @RequestParam(value = "movieDescription", required = false) String movieDescription,
                                     @RequestParam(value = "movieLanguage", required = false) String movieLanguage) {
        Movie movie;
        try {
            movie = movieService.getMovieByID(movieID);
        } catch (MovieNotFoundException e) {
            return "admin_movies_all_view";
        }
        movieService.updateMovie(movie.getMovieID(), movieName, movieCategory, movieDescription, movieLanguage);
        return "admin_movies_all_view";
    }

    @PostMapping("movies/all_add")
    public String addMovie(HttpSession session, @RequestParam("password") String password,
                           @RequestParam("movie_name") String movieName, @RequestParam("movie_category") String movieCategory,
                           @RequestParam(value = "movie_description", required = false) String movieDescription,
                           @RequestParam(value = "movie_language", required = false) String movieLanguage) {
        Admin admin = (Admin) session.getAttribute("user");
        movieService.addMovie(admin.getAdminId(), password, movieName, movieCategory, movieDescription, movieLanguage);
        return "admin_movies_all_view";
    }

    @DeleteMapping("movies/all_remove")
    public String removeMovie(@RequestParam("movie_id") Long movieID, @RequestParam("password") String password,
                              HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        movieService.removeMovie(movieID, admin.getAdminId(), password);
        return "admin_movies_all_view";
    }
}
