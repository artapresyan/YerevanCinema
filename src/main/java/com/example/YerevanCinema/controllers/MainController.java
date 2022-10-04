package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import com.example.YerevanCinema.services.validations.UserValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class MainController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;
    private final GmailClientServiceImpl gmailClientService;
    private final UserValidationService userValidationService;
    public MainController(CustomerServiceImpl customerService, AdminServiceImpl adminService, GmailClientServiceImpl gmailClientService, UserValidationService userValidationService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
        this.userValidationService = userValidationService;
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
                return "redirect:/customer/";
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

    @PostMapping("recover/password")
    public String recoverPassword(@RequestParam("pass_email") String email,@RequestParam("username") String username){
        try{
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(email)){
                gmailClientService.sendSimpleMessage(customer,"If you asked for password recovery contact us by email",
                        "RESET PASSWORD REQUEST");
            }
            return "redirect:/";
        }catch (UserNotFoundException | MessagingException ignored){
        }
        return "redirect:/recover";
    }

    @PostMapping("recover/username")
    public String recoverUsername(@RequestParam("pass_email") String email,@RequestParam("password") String password){
        try{
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer,password)){
                gmailClientService.sendSimpleMessage(customer,"If you asked for username recovery contact us by email",
                        "RESET USERNAME REQUEST");
            }
            return "redirect:/";
        }catch (UserNotFoundException | MessagingException ignored){
        }
        return "redirect:/recover";
    }
}
