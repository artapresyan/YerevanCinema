package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import com.example.YerevanCinema.services.implementations.MovieSessionServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;
    private final GmailClientServiceImpl gmailClientService;
    private final MovieSessionServiceImpl sessionService;
    public MainRestController(CustomerServiceImpl customerService, AdminServiceImpl adminService, GmailClientServiceImpl gmailClientService, MovieSessionServiceImpl sessionService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
        this.sessionService = sessionService;
    }

    @PostMapping("signup")
    public Customer signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                   @RequestParam("age") Integer age, @RequestParam("email") String email,
                                   @RequestParam("username") String username, @RequestParam("password") String password,
                                   @RequestParam("confirm_password") String confirmPassword) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            return customerService.registerCustomer(name, surname, age, username, email, password);
        }
        return null;
    }

    @PostMapping("login")
    public Object loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
                            HttpSession session) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password)) {
                session.setAttribute("user", customer);
                return customer;
            }
        } catch (UserNotFoundException ignored) {
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password)) {
                session.setAttribute("user", admin);
                return admin;
            }
        } catch (UserNotFoundException ignored) {
        }
        return null;
    }

    @PostMapping("contact")
    public String sendMessage(@RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(email, message);
            return mimeMessage.getContent().toString();
        } catch (MessagingException | IOException e) {
            return e.getMessage();
        }
    }

    @PostMapping("recover")
    public String recoverCustomerAccount(@RequestParam(value = "pass_email", required = false) String passEmail,
                                         @RequestParam(value = "password", required = false) String password,
                                         @RequestParam(value = "email", required = false) String email,
                                         @RequestParam(value = "username", required = false) String username) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer, password)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for username recovery contact us by email",
                        "RESET USERNAME REQUEST");
            }
            return "SENT";
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(passEmail)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for password recovery contact us by email",
                        "RESET PASSWORD REQUEST");
            }
            return "SENT";
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        return "ERROR while trying to send verification mail";
    }

    @GetMapping("sessions")
    public List<MovieSession> getSessions() {
        return sessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(7)))
                .collect(Collectors.toList());
    }
}
