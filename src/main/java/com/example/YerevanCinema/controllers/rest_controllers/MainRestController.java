package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;
    private final GmailClientServiceImpl gmailClientService;
    private final MovieSessionServiceImpl sessionService;
    private final JwtTokenServiceImpl jwtTokenService;

    private final CustomerValidationService customerValidationService;
    private final PasswordEncoder passwordEncoder;

    public MainRestController(CustomerServiceImpl customerService, AdminServiceImpl adminService,
                              GmailClientServiceImpl gmailClientService, MovieSessionServiceImpl sessionService,
                              JwtTokenServiceImpl jwtTokenService, CustomerValidationService customerValidationService,
                              PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
        this.sessionService = sessionService;
        this.jwtTokenService = jwtTokenService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("signup")
    public ResponseEntity signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                         @RequestParam("age") Integer age, @RequestParam("email") String email,
                                         @RequestParam("username") String username, @RequestParam("password") String password,
                                         @RequestParam("confirm_password") String confirmPassword) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password,
                    customerValidationService);
            return customer == null ? ResponseEntity.status(400).body("Not Registered") : ResponseEntity.ok(customer);
        }
        return null;
    }

    @PostMapping("login")
    public ResponseEntity loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
                                    HttpSession session) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password)) {

                session.setAttribute("user", customer);
                return ResponseEntity.ok(customer);
            }
        } catch (UserNotFoundException ignored) {
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password, passwordEncoder)) {
                session.setAttribute("user", admin);
                return ResponseEntity.ok(admin);
            }
        } catch (UserNotFoundException ignored) {
        }
        return ResponseEntity.notFound().build();
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
