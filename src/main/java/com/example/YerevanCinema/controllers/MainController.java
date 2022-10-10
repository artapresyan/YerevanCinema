package com.example.YerevanCinema.controllers;

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
import com.example.YerevanCinema.services.validations.AdminValidationService;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final GmailClientServiceImpl gmailClientService;
    private final AdminValidationService userValidationService;
    private final MovieSessionServiceImpl sessionService;
    private final CustomerValidationService customerValidationService;
    private final PasswordEncoder passwordEncoder;

    public MainController(CustomerServiceImpl customerService, GmailClientServiceImpl gmailClientService,
                          AdminValidationService userValidationService,MovieSessionServiceImpl sessionService,
                          PasswordEncoder passwordEncoder, CustomerValidationService customerValidationService) {
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
        this.userValidationService = userValidationService;
        this.sessionService = sessionService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getMainPage() {
        return "no_auth_main_view";
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
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password,
                    customerValidationService, passwordEncoder);
            if (customer != null) {
                session.setAttribute("customer", customer);
                return "redirect:/login";
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

    @PostMapping("contact_post")
    public String sendMessage(@RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            userValidationService.validateEmail(email);
            gmailClientService.getSimpleMessage(email, message);
        } catch (MessagingException | RegisteredEmailException | IOException e) {
            return "redirect:/contact";
        }
        return "redirect:/";
    }

    @GetMapping("sessions")
    public String getSessions(Model model) {
        List<MovieSession> movieSessions = sessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(8)) &&  LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isAfter(LocalDateTime.now().minusDays(1))).collect(Collectors.toList());
        model.addAttribute("movie_sessions", movieSessions);
        return "no_auth_sessions_view";
    }

    @GetMapping("recover")
    public String getRecoverPage() {
        return "recover_view";
    }

    @PostMapping("recover_username")
    public String recoverCustomerUsername(@RequestParam("password") String password,
                                          @RequestParam("email") String email) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer, password, passwordEncoder)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for username recovery contact us by email",
                        "RESET USERNAME REQUEST");
                return "redirect:/";
            }
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        return "recover_view";
    }

    @PostMapping("recover_password")
    public String recoverCustomerPassword(@RequestParam("pass_email") String passEmail,
                                          @RequestParam("username") String username) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(passEmail)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for password recovery contact us by email",
                        "RESET PASSWORD REQUEST");
            }
            return "redirect:/";
        } catch (UserNotFoundException |
                 MessagingException ignored) {
        }
        return "recover_view";
    }

    @GetMapping("home")
    public String getHomePage(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
            Admin admin = adminDetails.getAdmin();
            session.setAttribute("admin", admin);
            model.addAttribute("admin", admin);
            return "redirect:/admin/";
        } catch (ClassCastException ignored) {
        }
        try {
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            Customer customer = customerDetails.getCustomer();
            session.setAttribute("customer", customer);
            model.addAttribute("customer", customer);
            return "redirect:/customer/";

        } catch (ClassCastException e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}
