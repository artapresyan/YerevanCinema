package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("rest/api/customer/")
public class CustomerRestController {
    @Value("${qr.path}")
    private String qrPath;
    private final MovieSessionServiceImpl movieSessionService;
    private final CustomerServiceImpl customerService;
    private final GmailClientServiceImpl gmailClientService;
    private final TicketServiceImpl ticketService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerValidationService customerValidationService;
    private final QRCodeServiceImpl qrCodeService;

    public CustomerRestController(MovieSessionServiceImpl movieSessionService, CustomerServiceImpl customerService,
                                  GmailClientServiceImpl gmailClientService, TicketServiceImpl ticketService,
                                  PasswordEncoder passwordEncoder, CustomerValidationService customerValidationService,
                                  QRCodeServiceImpl qrCodeService) {
        this.movieSessionService = movieSessionService;
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
        this.ticketService = ticketService;
        this.passwordEncoder = passwordEncoder;
        this.customerValidationService = customerValidationService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public ResponseEntity<Customer> getCustomerMainPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("about")
    public ResponseEntity<Customer> getAboutPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("contact")
    public ResponseEntity<Customer> getContactPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("contact_post")
    public ResponseEntity<String> sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("customer");
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
            return ResponseEntity.ok(mimeMessage.getContent().toString());
        } catch (MessagingException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("details")
    public ResponseEntity<Customer> getAccountDetailsPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("details/remove")
    public ResponseEntity<Customer> getDeactivationPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("details/remove_account")
    public ResponseEntity<Customer> removeAccount(HttpSession session, String password) {
        Customer customer = (Customer) session.getAttribute("customer");
        customer = customerService.selfRemoveCustomer(customer.getCustomerID(), password, passwordEncoder);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("details/edit")
    public ResponseEntity<Customer> getAccountDetailsEditPage(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("details/edit_change")
    public ResponseEntity<Customer> updateAccountDetails(@RequestParam(value = "customer_name", required = false) String newName,
                                                         @RequestParam(value = "customer_surname", required = false) String newSurname,
                                                         @RequestParam(value = "customer_age", required = false) Integer newAge,
                                                         @RequestParam(value = "customer_username", required = false) String newUsername,
                                                         @RequestParam(value = "customer_email", required = false) String newEmail,
                                                         @RequestParam(value = "new_password", required = false) String newPassword,
                                                         @RequestParam(value = "new_password_confirm", required = false) String confirmNewPassword,
                                                         @RequestParam("password") String password, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (newPassword.equals(confirmNewPassword)) {
            customer = customerService.updateCustomerData(customer.getCustomerID(), newName, newSurname, newAge, newUsername,
                    newEmail, password, newPassword, customerValidationService, passwordEncoder);
            if (customer != null) {
                return ResponseEntity.ok(customer);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("tickets")
    public ResponseEntity<List<Ticket>> getPurchasedTickets(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        return ResponseEntity.ok(ticketService.getAllTickets().stream()
                .filter(ticket -> ticket.getCustomer().getCustomerID().equals(customer.getCustomerID()))
                .collect(Collectors.toList()));
    }

    @GetMapping("sessions")
    public ResponseEntity<List<MovieSession>> getSessionsPage() {
        return ResponseEntity.ok(movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(14))).collect(Collectors.toList()));
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

    @PostMapping("session/purchase")
    public ResponseEntity<String> sendTicketToCustomer(@RequestParam("selected_session_id") Long movieSessionID,
                                                       @RequestParam("ticket_count") Integer ticketCount,
                                                       @RequestParam("password") String password, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
            try {
                MovieSession movieSession = movieSessionService.getMovieSessionByID(movieSessionID);
                for (int i = 0; i < ticketCount; i++) {
                    Ticket ticket = ticketService.addTicket(customer, movieSession);
                    qrCodeService.generateQRCodeImage(customer, ticket.getTicketID(), movieSession);
                    MimeMessage mimeMessage = gmailClientService.sendMessageWithAttachment(customer,
                            String.format(qrPath, customer.getCustomerID(), customer.getCustomerEmail(), ticket.getTicketID()));
                    if (mimeMessage == null) {
                        return ResponseEntity.badRequest().build();
                    }
                }
                return ResponseEntity.ok("ALL TICKETS SENT");
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("tickets/resend")
    public ResponseEntity<String> resendTicketToCustomer(@RequestParam("ticket_id") Long ticketID,
                                         @RequestParam("password") String password, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
            try {
                Ticket ticket = ticketService.getTicketByID(ticketID);
                if (customer.getCustomerID().equals(ticket.getCustomer().getCustomerID())) {
                    qrCodeService.generateQRCodeImage(customer, ticketID, ticket.getMovieSession());
                    MimeMessage mimeMessage = gmailClientService.sendMessageWithAttachment(customer,
                            String.format(qrPath, customer.getCustomerID(), customer.getCustomerEmail(), ticket.getTicketID()));
                    if (mimeMessage != null){
                        return ResponseEntity.ok(mimeMessage.getContent().toString());
                    }else {
                        return ResponseEntity.badRequest().build();
                    }
                } else
                    throw new TicketNotFoundException("No Such Ticket");
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private List<MovieSession> getSelectedMovieSessions(String keyValue, String selected) throws MovieNotFoundException, HallNotFoundException {
        switch (keyValue) {
            case "Movie":
                return movieSessionService.getAllMovieSessions().stream()
                        .filter(movieSession -> movieSession.getMovie().getMovieName().equals(selected))
                        .collect(Collectors.toList());
            case "Category":
                return movieSessionService.getAllMovieSessions().stream()
                        .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(selected))
                        .collect(Collectors.toList());
            case "Price":
                return movieSessionService.getAllMovieSessions().stream()
                        .filter(movieSession -> movieSession.getMovieSessionPrice().equals(Integer.parseInt(selected)))
                        .collect(Collectors.toList());
            case "Hall":
                return movieSessionService.getAllMovieSessions().stream()
                        .filter(movieSession -> movieSession.getHall().getHallName().equals(selected))
                        .collect(Collectors.toList());
        }
        return List.of();
    }
}
