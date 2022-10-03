package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class MainController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;

    private final GmailClientServiceImpl gmailClientService;

    public MainController(CustomerServiceImpl customerService, AdminServiceImpl adminService, GmailClientServiceImpl gmailClientService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
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
                            HttpSession session, ModelMap model) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password)) {
                session.setAttribute("user", customer);
                model.addAttribute("user", customer);
                return "redirect:/customer/";
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password)) {
                session.setAttribute("user", admin);
                model.addAttribute("user", admin);
                return "redirect:/admin/";
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/login";
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

    @PostMapping("contact")
    public String sendMessage(@RequestParam(value = "name", required = false) String name,
                              @RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            gmailClientService.getSimpleMessage(email, message);
        } catch (MessagingException e) {
            return "redirect:/contact";
        }
        return "redirect:/";
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
