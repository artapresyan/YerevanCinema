package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    @Value("${qr.path}")
    private String qrPath;
    private final MovieSessionServiceImpl movieSessionService;
    private final CustomerServiceImpl customerService;
    private final GmailClientServiceImpl gmailClientService;
    private final TicketServiceImpl ticketService;

    private final QRCodeServiceImpl qrCodeService;

    public CustomerController(MovieSessionServiceImpl movieSessionService, CustomerServiceImpl customerService,
                              GmailClientServiceImpl gmailClientService, TicketServiceImpl ticketService,
                              QRCodeServiceImpl qrCodeService) {
        this.movieSessionService = movieSessionService;
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
        this.ticketService = ticketService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public String getCustomerMainPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_main_view";
    }

    @GetMapping("about")
    public String getAboutPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_about_view";
    }

    @GetMapping("contact")
    public String getContactPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_contact_view";
    }

    @PostMapping("contact")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("user");
            gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
        } catch (MessagingException e) {
            return "customer_contact_view";
        }
        return "customer_main_view";
    }

    @GetMapping("details")
    public String getAccountDetailsPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_details_view";
    }

    @GetMapping("details/edit")
    public String getAccountDetailsEditPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "customer_details_edit_view";
    }

    @PutMapping("details/edit")
    public String updateAccountDetails(@RequestParam(value = "name", required = false) String newName,
                                       @RequestParam(value = "surname", required = false) String newSurname,
                                       @RequestParam(value = "age", required = false) Integer newAge,
                                       @RequestParam(value = "username", required = false) String newUsername,
                                       @RequestParam(value = "email", required = false) String newEmail,
                                       @RequestParam(value = "password", required = false) String newPassword,
                                       HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        customerService.updateCustomerData(customer.getCustomerID(), newName, newSurname, newAge, newUsername,
                newEmail, customer.getCustomerPassword(), newPassword);
        try {
            customer = customerService.getCustomerByID(customer.getCustomerID());
            session.setAttribute("user", customer);
            model.addAttribute("user", customer);
            return "customer_details_edit_view";
        } catch (UserNotFoundException e) {
            return "redirect:/details/edit";
        }
    }

    @GetMapping("tickets")
    public String getPurchasedTickets(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("tickets", customer.getCustomerTickets());
        return "tickets_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        List<MovieSession> sessions = movieSessionService.getAllMovieSessions().stream().filter(movieSession ->
                        movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(14)))
                .collect(Collectors.toList());
        model.addAttribute("user", customer);
        model.addAttribute("sessions", sessions);
        return "customer_sessions_view";
    }

    @GetMapping("sessions/movie")
    public String getSessionsByMovieName(@RequestParam("movie_name") String name, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieName().equals(name))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "sessions_selected_view";
    }

    @GetMapping("sessions/category")
    public String getSessionsByMovieCategory(@RequestParam("movie_category") String movieCategory, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(movieCategory))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "sessions_selected_view";
    }

    @GetMapping("sessions/start")
    public String getSessionsByStart(@RequestParam("movie_start") LocalDateTime movieStart, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().isEqual(movieStart))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "sessions_selected_view";
    }

    @GetMapping("sessions/hall")
    public String getSessionsByHall(@RequestParam("movie_hall") Long hallID, Model model) {
        List<MovieSession> movieSessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getHall().getHallID().equals(hallID))
                .collect(Collectors.toList());
        model.addAttribute("movies", movieSessions);
        return "sessions_selected_view";
    }

    @PostMapping("sessions/*")
    public String purchaseSessionsByMovieName(@RequestParam("movieSessionID") Long movieSessionID, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        try {
            MovieSession movieSession = movieSessionService.getMovieSessionByID(movieSessionID);
            Ticket ticket = ticketService.addTicket(customer, movieSession);
            qrCodeService.generateQRCodeImage(customer);
            gmailClientService.sendMessageWithAttachment(customer, String.format(qrPath, ticket.getTicketID(),
                    customer.getCustomerUsername()));
        } catch (Exception e) {
            return "redirect:/customer/sessions";
        }
        return "redirect:/customer/tickets";
    }
}
