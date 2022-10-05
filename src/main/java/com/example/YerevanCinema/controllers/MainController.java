package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import com.example.YerevanCinema.services.implementations.MovieSessionServiceImpl;
import com.example.YerevanCinema.services.validations.UserValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;
    private final GmailClientServiceImpl gmailClientService;
    private final UserValidationService userValidationService;

    private final MovieSessionServiceImpl sessionService;

    public MainController(CustomerServiceImpl customerService, AdminServiceImpl adminService, GmailClientServiceImpl gmailClientService, UserValidationService userValidationService, MovieSessionServiceImpl sessionService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
        this.userValidationService = userValidationService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public String getMainPage() {
        return "no_auth_main_view";
    }

    @GetMapping("login")
    public String getLoginPage() {
        return "login_view";
    }

    @PostMapping("login")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
                            HttpSession session, Model model) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password)) {
                session.setAttribute("user", customer);
                model.addAttribute("user", customer);
                return "customer_main_view";
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password)) {
                session.setAttribute("user", admin);
                model.addAttribute("user", admin);
                return "admin_main_view";
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "login_view";
    }

    @GetMapping("signup")
    public String getSighUpPage() {
        return "signup_view";
    }

    @PostMapping("signup")
    public String signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                 @RequestParam("age") Integer age, @RequestParam("email") String email,
                                 @RequestParam("username") String username, @RequestParam("password") String password,
                                 @RequestParam("confirm_password") String confirmPassword, HttpSession session) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password);
            if (customer != null) {
                session.setAttribute("user", customer);
                return "login_view";
            }
        }
        return "signup_view";
    }

    @GetMapping("about")
    public String getAboutPage() {
        return "no_auth_about_view";
    }

    @GetMapping("contact")
    public String getContactPage() {
        return "no_auth_contact_view";
    }

    @PostMapping("contact")
    public String sendMessage(@RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            userValidationService.validateEmail(email);
            gmailClientService.getSimpleMessage(email, message);
        } catch (MessagingException | RegisteredEmailException | IOException e) {
            return "no_auth_contact_view";
        }
        return "no_auth_main_view";
    }

    @GetMapping("sessions")
    public String getSessions(Model model) {
        List<MovieSession> movieSessions = sessionService.getAllMovieSessions().stream()
                .filter(movieSession -> movieSession.getMovieSessionStart().isBefore(LocalDateTime.now().plusDays(7)))
                .collect(Collectors.toList());
        model.addAttribute("movie_sessions", movieSessions);
        return "no_auth_sessions_view";
    }

    @GetMapping("recover")
    public String getRecoverPage() {
        return "recover_view";
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
            return "login_view";
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(passEmail)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for password recovery contact us by email",
                        "RESET PASSWORD REQUEST");
            }
            return "login_view";
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        return "recover_view";
    }
}
