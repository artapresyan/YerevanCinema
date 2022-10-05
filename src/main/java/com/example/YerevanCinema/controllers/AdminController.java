package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
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
    private final QRCodeServiceImpl qrCodeService;
    public AdminController(GmailClientServiceImpl gmailClientService, AdminServiceImpl adminService,
                           TicketServiceImpl ticketService, MovieSessionServiceImpl movieSessionService, QRCodeServiceImpl qrCodeService) {
        this.gmailClientService = gmailClientService;
        this.adminService = adminService;
        this.ticketService = ticketService;
        this.movieSessionService = movieSessionService;
        this.qrCodeService = qrCodeService;
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

    @PostMapping("contact")
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

    @PutMapping("details/edit")
    public String updateAccountDetails(@RequestParam(value = "name", required = false) String newName,
                                       @RequestParam(value = "surname", required = false) String newSurname,
                                       @RequestParam(value = "username", required = false) String newUsername,
                                       @RequestParam(value = "email", required = false) String newEmail,
                                       @RequestParam(value = "password", required = false) String newPassword,
                                       HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        adminService.updateAdminData(admin.getAdminId(), newName, newSurname, newUsername,
                newEmail, admin.getAdminPassword(), newPassword);
        try {
            admin = adminService.getAdminByID(admin.getAdminId());
            session.setAttribute("user", admin);
            model.addAttribute("user", admin);
            return "admin_details_edit_view";
        } catch (UserNotFoundException e) {
            return "redirect:/admin/details/edit";
        }
    }

    @GetMapping("tickets")
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


}
