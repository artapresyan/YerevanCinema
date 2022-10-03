package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MainController {

    private final CustomerServiceImpl customerService;

    public MainController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String getMainPage() {
        return "no_auth_main_view";
    }

    @GetMapping("login")
    public String getLoginPage() {
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
                                 @RequestParam("confirm_password") String confirmPassword, Model model) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password);
            if (customer != null) {
                model.addAttribute("customer", customer);
                return "main_view";
            }
        }
        return "login_view";
    }

    @GetMapping("about")
    public String getAboutPage() {
        return "no_auth_about_view";
    }

    @GetMapping("contact")
    public String getContactPage() {
        return "no_auth_contact_view";
    }

    @GetMapping("sessions")
    public String getSessions() {
        return "no_auth_sessions_view";
    }

    @GetMapping("recover")
    public String getRecoverPage() {
        return "recover_view";
    }
}
