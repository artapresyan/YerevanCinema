package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Seat;
import com.example.YerevanCinema.exceptions.MovieSessionNotFoundException;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import com.example.YerevanCinema.services.implementations.MovieSessionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    private final MovieSessionServiceImpl movieSessionService;

    private final GmailClientServiceImpl gmailClientService;

    public CustomerController(MovieSessionServiceImpl movieSessionService, GmailClientServiceImpl gmailClientService) {
        this.movieSessionService = movieSessionService;
        this.gmailClientService = gmailClientService;
    }

    @GetMapping
    public String getCustomerHomePage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "main_view";
    }

    @GetMapping("about")
    public String getAboutPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "about_view";
    }

    @GetMapping("contact")
    public String getContactPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "contact_view";
    }

    @PostMapping("contact")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("user");
            gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
        } catch (MessagingException e) {
            return "contact_view";
        }
        return "main_view";
    }

    @GetMapping("details")
    public String getAccountDetails(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_details_view";
    }

    @GetMapping("tickets")
    public String getPurchasedTickets(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "tickets_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        List<MovieSession> sessions = movieSessionService.getAllMovieSessions().stream().filter(movieSession ->
                        movieSession.getMovieSessionStart().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                .collect(Collectors.toList());
        model.addAttribute("user", customer);
        model.addAttribute("sessions", sessions);
        return "sessions_view";
    }

    @GetMapping("/sessions/seats")
    public String getSessionSeats(HttpSession session, @RequestParam("movieSessionID") Long movieSessionID,
                                  Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        try {
            MovieSession movieSession = movieSessionService.getMovieSessionByID(movieSessionID);
            List<Seat> seats = movieSession.getHall().getHallSeats().stream()
                    .filter(seat -> !seat.getIsSold()).collect(Collectors.toList());
            model.addAttribute("user", customer);
            model.addAttribute("seats", seats);
            return "session_seats_view";
        } catch (MovieSessionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
