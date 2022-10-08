package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.*;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import com.example.YerevanCinema.services.validations.HallValidationService;
import com.example.YerevanCinema.services.validations.MovieSessionValidationService;
import com.example.YerevanCinema.services.validations.MovieValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final MovieValidationService movieValidationService;
    private final MovieSessionValidationService movieSessionValidationService;

    private final HallValidationService hallValidationService;
    private final PasswordEncoder passwordEncoder;

    private final AdminValidationService userValidationService;

    public AdminController(GmailClientServiceImpl gmailClientService, AdminServiceImpl adminService,
                           TicketServiceImpl ticketService, MovieSessionServiceImpl movieSessionService,
                           MovieServiceImpl movieService, HallServiceImpl hallService,
                           CustomerServiceImpl customerService, MovieValidationService movieValidationService,
                           MovieSessionValidationService movieSessionValidationService,
                           HallValidationService hallValidationService, PasswordEncoder passwordEncoder,
                           AdminValidationService userValidationService) {
        this.gmailClientService = gmailClientService;
        this.adminService = adminService;
        this.ticketService = ticketService;
        this.movieSessionService = movieSessionService;
        this.movieService = movieService;
        this.hallService = hallService;
        this.customerService = customerService;
        this.movieValidationService = movieValidationService;
        this.movieSessionValidationService = movieSessionValidationService;
        this.hallValidationService = hallValidationService;
        this.passwordEncoder = passwordEncoder;
        this.userValidationService = userValidationService;
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
    public String sendMessage(HttpSession session, @RequestParam("message") String message, Model model) {
        try {
            Admin admin = (Admin) session.getAttribute("user");
            model.addAttribute("user", admin);
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

    @PostMapping("details/edit_put")
    public String editAccountDetails(@RequestParam(value = "admin_name", required = false) String newName,
                                     @RequestParam(value = "admin_surname", required = false) String newSurname,
                                     @RequestParam(value = "admin_username", required = false) String newUsername,
                                     @RequestParam(value = "admin_email", required = false) String newEmail,
                                     @RequestParam(value = "admin_new_password", required = false) String newPassword,
                                     @RequestParam(value = "admin_new_password_confirm", required = false) String confirmNewPassword,
                                     @RequestParam("admin_password") String password, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        if (newPassword.equals(confirmNewPassword))
            adminService.updateAdminData(admin.getAdminId(), newName, newSurname, newUsername,
                    newEmail, password, newPassword, userValidationService, passwordEncoder);
        try {
            admin = adminService.getAdminByID(admin.getAdminId());
            session.setAttribute("user", admin);
            model.addAttribute("user", admin);
            return "redirect:/admin/details/edit";
        } catch (UserNotFoundException e) {
            return "admin_details_edit_view";
        }
    }

    @GetMapping("tickets/all")
    public String getAllPurchasedTickets(HttpSession session, Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("tickets", tickets);
        return "admin_tickets_all_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage(HttpSession session, Model model) {
        List<MovieSession> sessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(14))).collect(Collectors.toList());
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("movie_sessions", sessions);
        return "admin_sessions_view";
    }

    @GetMapping("sessions/all")
    public String getAllSessions(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions();
        model.addAttribute("movie_sessions_all", movieSessions);
        return "admin_sessions_all_view";
    }

    @PostMapping("sessions/all")
    public String changeSessionDetails(@RequestParam("movieSessionID") Long movieSessionID,
                                       @RequestParam(value = "movieSessionStart", required = false) String movieSessionStart,
                                       @RequestParam(value = "movieSessionEnd", required = false) String movieSessionEnd,
                                       @RequestParam(value = "movieSessionPrice", required = false) Integer movieSessionPrice,
                                       @RequestParam(value = "movieSessionHall", required = false) Long movieSessionHallID,
                                       @RequestParam(value = "movieSessionMovie", required = false) Long movieSessionMovieID,
                                       HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
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
        movieSessionService.updateMovieSession(movieSessionID, movieSessionStart, movieSessionEnd,
                movieSessionPrice, hall, movie, admin, movieSessionValidationService);
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions();
        model.addAttribute("movie_sessions_all", movieSessions);
        return "redirect:/admin/sessions/all";
    }

    @PostMapping("sessions/all_add")
    public String addSession(@RequestParam("movie_name") String movieName,
                             @RequestParam("session_start") String sessionStart,
                             @RequestParam("session_end") String  sessionEnd,
                             @RequestParam("session_hall") String sessionHallName,
                             @RequestParam("session_price") Integer sessionPrice,
                             @RequestParam("password") String password, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
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
        movieSessionService.addMovieSession(sessionStart, sessionEnd, sessionPrice, hall, movie, admin, password,
                passwordEncoder, movieSessionValidationService);
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions();
        model.addAttribute("movie_sessions_all", movieSessions);
        return "redirect:/admin/sessions/all";
    }

    @PostMapping("sessions/all_remove")
    public String removeSession(@RequestParam("session_id") Long movieSessionID,
                                @RequestParam("password") String password, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        movieSessionService.removeMovieSession(admin, password, movieSessionID, passwordEncoder);
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions();
        model.addAttribute("movie_sessions_all", movieSessions);
        return "redirect:/admin/sessions/all";
    }

    @GetMapping("sessions/movie")
    public String getSessionsByMovieName(@RequestParam("movie_name") String name, HttpSession session, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieName().equals(name))
                .collect(Collectors.toList());
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("movie_sessions_movie", movieSessions);
        return "admin_sessions_movie_selected_view";
    }

    @GetMapping("sessions/category")
    public String getSessionsByMovieCategory(@RequestParam("movie_category") String movieCategory, HttpSession session,
                                             Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(movieCategory))
                .collect(Collectors.toList());
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("movie_session_category", movieSessions);
        return "admin_sessions_category_selected_view";
    }

    @GetMapping("sessions/start")
    public String getSessionsByStart(@RequestParam("movie_start") String movieStart, HttpSession session, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().equals(movieStart))
                .collect(Collectors.toList());
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("movie_sessions_start", movieSessions);
        return "admin_sessions_start_selected_view";
    }

    @GetMapping("sessions/hall")
    public String getSessionsByHall(@RequestParam("movie_hall") Long hallID, HttpSession session, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getHall().getHallID().equals(hallID))
                .collect(Collectors.toList());
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("movie_sessions_hall", movieSessions);
        return "admin_sessions_hall_selected_view";
    }

    @GetMapping("customers/all")
    public String getAllCustomers(HttpSession session, Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        model.addAttribute("customers", customers);
        return "admin_customers_all_view";
    }

    @PostMapping("customers/all_remove")
    public String removeCustomer(@RequestParam(value = "customer_id") Long customerID,
                                 @RequestParam(value = "password") String password, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        customerService.adminRemoveCustomer(customerID, admin, password, passwordEncoder);
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "redirect:/adimn/customers/all";
    }

    @GetMapping("movies/all")
    public String getAllMovies(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("allMovies", movies);
        return "admin_movies_all_view";
    }

    @PostMapping("movies/all_update")
    public String updateMovieDetails(@RequestParam("movieID") Long movieID,
                                     @RequestParam(value = "movieName", required = false) String movieName,
                                     @RequestParam(value = "movieCategory", required = false) String movieCategory,
                                     @RequestParam(value = "movieDescription", required = false) String movieDescription,
                                     @RequestParam(value = "movieLanguage", required = false) String movieLanguage,
                                     HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        Movie movie;
        try {
            movie = movieService.getMovieByID(movieID);
        } catch (MovieNotFoundException e) {
            return "redirect:/admin/movies/all";
        }
        movieService.updateMovie(movie.getMovieID(), movieName, movieCategory, movieDescription, movieLanguage,
                movieValidationService);
        return "redirect:/admin/movies/all";
    }

    @PostMapping("movies/all_add")
    public String addMovie(@RequestParam("password") String password,
                           @RequestParam("movie_name") String movieName, @RequestParam("movie_category") String movieCategory,
                           @RequestParam(value = "movie_description", required = false) String movieDescription,
                           @RequestParam(value = "movie_language", required = false) String movieLanguage,
                           HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        movieService.addMovie(admin.getAdminId(), password, movieName, movieCategory, movieDescription, movieLanguage,
                adminService, passwordEncoder, movieValidationService);
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("allMovies", movies);
        return "redirect:/admin/movies/all";
    }

    @PostMapping("movies/all_remove")
    public String removeMovie(@RequestParam("movie_id") Long movieID, @RequestParam("password") String password,
                              HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        movieService.removeMovie(movieID, admin.getAdminId(), password, adminService, passwordEncoder);
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("allMovies", movies);
        return "redirect:/admin/movies/all";
    }

    @GetMapping("halls/all")
    public String getHalls(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        List<Hall> halls = hallService.getAllHalls();
        model.addAttribute("allHalls", halls);
        return "admin_halls_all_view";
    }

    @PostMapping("halls/all_add")
    public String addHall(@RequestParam("hall_name") String hallName, @RequestParam("hall_capacity") Integer hallCapacity,
                          @RequestParam("password") String password, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        model.addAttribute("user", admin);
        hallService.addHall(admin.getAdminId(), password, hallName, hallCapacity, hallValidationService,
                adminService, passwordEncoder);
        List<Hall> halls = hallService.getAllHalls();
        model.addAttribute("allHalls", halls);
        return "redirect:/admin/halls/all";
    }
}
