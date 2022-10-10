package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.entityDetails.AdminDetails;
import com.example.YerevanCinema.entities.entityDetails.CustomerDetails;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import com.example.YerevanCinema.services.implementations.MovieSessionServiceImpl;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MovieSessionServiceImpl sessionService;
    private final CustomerValidationService customerValidationService;
    private final GmailClientServiceImpl gmailClientService;
    private final PasswordEncoder passwordEncoder;

    public MainRestController(CustomerServiceImpl customerService, MovieSessionServiceImpl sessionService,
                              PasswordEncoder passwordEncoder, CustomerValidationService customerValidationService,
                              GmailClientServiceImpl gmailClientService) {
        this.customerService = customerService;
        this.sessionService = sessionService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
        this.gmailClientService = gmailClientService;
    }

    @PostMapping("signup")
    public ResponseEntity<Customer> signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                 @RequestParam("age") Integer age, @RequestParam("email") String email,
                                 @RequestParam("username") String username, @RequestParam("password") String password,
                                 @RequestParam("confirm_password") String confirmPassword, HttpSession session) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password,
                    customerValidationService, passwordEncoder);
            if (customer != null) {
                session.setAttribute("customer", customer);
                return ResponseEntity.ok(customer);
            }else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("contact_post")
    public ResponseEntity<String> sendMessage(@RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            customerValidationService.validateEmail(email);
            MimeMessage mimeMessage = gmailClientService.getSimpleMessage(email, message);
            return ResponseEntity.ok(mimeMessage.getContent().toString());
        } catch (MessagingException | RegisteredEmailException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("sessions")
    public ResponseEntity<List<MovieSession>> getSessions() {
        return ResponseEntity.ok(sessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(8)) &&  LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isAfter(LocalDateTime.now().minusDays(1))).collect(Collectors.toList()));
    }

    @PostMapping("recover_username")
    public ResponseEntity<String> recoverCustomerUsername(@RequestParam("password") String password,
                                          @RequestParam("email") String email) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer, password, passwordEncoder)) {
                MimeMessage mimeMessage = gmailClientService.sendSimpleMessage(customer,
                        "If you asked for username recovery contact us by email", "RESET USERNAME REQUEST");
                return ResponseEntity.ok(mimeMessage.getContent().toString());
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (UserNotFoundException | IOException | MessagingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("recover_password")
    public ResponseEntity<String> recoverCustomerPassword(@RequestParam("pass_email") String passEmail,
                                          @RequestParam("username") String username) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(passEmail)) {
                MimeMessage mimeMessage = gmailClientService.sendSimpleMessage(customer,
                        "If you asked for password recovery contact us by email", "RESET PASSWORD REQUEST");
                return ResponseEntity.ok(mimeMessage.getContent().toString());
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (UserNotFoundException | IOException | MessagingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("home")
    public ResponseEntity getHomePage(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
            Admin admin = adminDetails.getAdmin();
            session.setAttribute("admin", admin);
            return ResponseEntity.ok(admin);
        } catch (ClassCastException ignored) {
        }
        try {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            Customer customer = customerDetails.getCustomer();
            session.setAttribute("customer", customer);
            return ResponseEntity.ok(customer);

        } catch (ClassCastException e) {
           return ResponseEntity.badRequest().build();
        }
    }
}
