package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/api/customer/")
public class CustomerRestController {

    @Value("${qr.path}")
    private String qrPath;
    private final MovieSessionServiceImpl movieSessionService;
    private final CustomerServiceImpl customerService;
    private final GmailClientServiceImpl gmailClientService;
    private final TicketServiceImpl ticketService;
    private final QRCodeServiceImpl qrCodeService;
    private final CustomerValidationService customerValidationService;
    private final PasswordEncoder passwordEncoder;

    public CustomerRestController(MovieSessionServiceImpl movieSessionService, CustomerServiceImpl customerService,
                                  GmailClientServiceImpl gmailClientService, TicketServiceImpl ticketService,
                                  QRCodeServiceImpl qrCodeService, CustomerValidationService customerValidationService,
                                  PasswordEncoder passwordEncoder) {
        this.movieSessionService = movieSessionService;
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
        this.ticketService = ticketService;
        this.qrCodeService = qrCodeService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Customer getCustomerHomePage(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    @GetMapping("about")
    public Customer getAboutPage(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    @GetMapping("contact")
    public Customer getContactPage(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    @PostMapping("contact")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("user");
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
            return mimeMessage.getContent().toString();
        } catch (MessagingException | IOException e) {
            return e.getMessage();
        }
    }

    @GetMapping("details")
    public Customer getAccountDetailsPage(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    @GetMapping("details/edit")
    public Customer getAccountDetailsEditPage(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    @PutMapping("details/edit")
    public Customer updateAccountDetails(@RequestParam(value = "name", required = false) String newName,
                                         @RequestParam(value = "surname", required = false) String newSurname,
                                         @RequestParam(value = "age", required = false) Integer newAge,
                                         @RequestParam(value = "username", required = false) String newUsername,
                                         @RequestParam(value = "email", required = false) String newEmail,
                                         @RequestParam(value = "password", required = false) String newPassword,
                                         HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        customerService.updateCustomerData(customer.getCustomerID(), newName, newSurname, newAge, newUsername,
                newEmail, customer.getCustomerPassword(), newPassword, customerValidationService, passwordEncoder);
        try {
            customer = customerService.getCustomerByID(customer.getCustomerID());
            session.setAttribute("user", customer);
            return customer;
        } catch (UserNotFoundException e) {
            return null;
        }
    }

    @GetMapping("tickets")
    public List<Ticket> getPurchasedTickets(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        return customer.getCustomerTickets();
    }

    @GetMapping("sessions")
    public List<MovieSession> getSessionsPage() {
        return movieSessionService.getAllMovieSessions().stream().filter(movieSession ->
                        movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(14)))
                .collect(Collectors.toList());
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

    @PostMapping("sessions/*")
    public String purchaseSessions(@RequestParam("movieSessionID") Long movieSessionID, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("user");
        try {
            MovieSession movieSession = movieSessionService.getMovieSessionByID(movieSessionID);
            Ticket ticket = ticketService.addTicket(customer, movieSession);
            qrCodeService.generateQRCodeImage(customer);
            MimeMessage mimeMessage = gmailClientService.sendMessageWithAttachment(customer,
                    String.format(qrPath, ticket.getTicketID(), customer.getCustomerUsername()));
            return mimeMessage.getContent().toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
