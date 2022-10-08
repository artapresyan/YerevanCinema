package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.MovieSessionServiceImpl;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;

    private final MovieSessionServiceImpl sessionService;

    private final CustomerValidationService customerValidationService;
    private final PasswordEncoder passwordEncoder;

    public MainRestController(CustomerServiceImpl customerService, AdminServiceImpl adminService,
                              MovieSessionServiceImpl sessionService, PasswordEncoder passwordEncoder,
                              CustomerValidationService customerValidationService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.sessionService = sessionService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity loginUser(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password, passwordEncoder)) {
                return ResponseEntity.ok(customer);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (UserNotFoundException ignored) {
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password, passwordEncoder)) {
                return ResponseEntity.ok(admin);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (UserNotFoundException ignored) {
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("signup")
    public ResponseEntity<Customer> signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                                   @RequestParam("age") Integer age, @RequestParam("email") String email,
                                                   @RequestParam("username") String username, @RequestParam("password") String password,
                                                   @RequestParam("confirm_password") String confirmPassword) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password,
                    customerValidationService, passwordEncoder);
            if (customer != null) {
                return ResponseEntity.ok(customer);
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("sessions")
    public ResponseEntity<List<MovieSession>> getSessions() {
        return ResponseEntity.ok(sessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(7)))
                .collect(Collectors.toList()));
    }
}
