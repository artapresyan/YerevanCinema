package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private final AdminValidationService userValidationService;
    private final JwtTokenServiceImpl jwtTokenService;
    private final MovieSessionServiceImpl sessionService;

    private final CustomerValidationService customerValidationService;
    private final PasswordEncoder passwordEncoder;

    public MainController(CustomerServiceImpl customerService, AdminServiceImpl adminService,
                          GmailClientServiceImpl gmailClientService, AdminValidationService userValidationService,
                          JwtTokenServiceImpl jwtTokenService, MovieSessionServiceImpl sessionService,
                          CustomerValidationService customerValidationService, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
        this.userValidationService = userValidationService;
        this.jwtTokenService = jwtTokenService;
        this.sessionService = sessionService;
        this.customerValidationService = customerValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getMainPage() {
        return "no_auth_main_view";
    }

    @GetMapping("login")
    public String getLoginPage() {
        return "login_view";
    }

    @PostMapping("login_load")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
                            HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password, passwordEncoder)) {
                String generatedToken = jwtTokenService.getCustomerJwtToken(customer.getCustomerUsername());
                request.getSession().setAttribute("user", customer);

                model.addAttribute("user", customer);
                return "customer_main_view";
            }
        } catch (UserNotFoundException ignored) {
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password, passwordEncoder)) {
                String generatedToken = jwtTokenService.getAdminJwtToken(admin.getAdminUsername());
                request.getSession().setAttribute("user", admin);
                response.addHeader("Authorization", generatedToken);
                model.addAttribute("user", admin);
                return "admin_main_view";
            }
        } catch (UserNotFoundException ignored) {
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
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password,
                    customerValidationService, passwordEncoder);
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

    @PostMapping("contact_post")
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

    @PostMapping("recover_username")
    public String recoverCustomerUsername(@RequestParam("password") String password,
                                          @RequestParam("email") String email) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer, password, passwordEncoder)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for username recovery contact us by email",
                        "RESET USERNAME REQUEST");
                return "login_view";
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
            return "login_view";
        } catch (UserNotFoundException |
                 MessagingException ignored) {
        }
        return "recover_view";
    }
}
