package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;

    private final GmailClientServiceImpl gmailClientService;

    public MainRestController(CustomerServiceImpl customerService, AdminServiceImpl adminService, GmailClientServiceImpl gmailClientService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.gmailClientService = gmailClientService;
    }


    @PostMapping("signup")
    public Customer signUpCustomer(@RequestParam("name") String name, @RequestParam("surname") String surname,
                                   @RequestParam("age") Integer age, @RequestParam("email") String email,
                                   @RequestParam("username") String username, @RequestParam("password") String password,
                                   @RequestParam("confirm_password") String confirmPassword, Model model) {
        if (customerService.confirmPassword(password, confirmPassword)) {
            Customer customer = customerService.registerCustomer(name, surname, age, username, email, password);
            if (customer != null) {
                model.addAttribute("customer", customer);
                return customer;
            }
        }
        return null;
    }

    @PostMapping("login")
    public Object loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
                            HttpSession session, ModelMap model) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customerService.passwordsAreMatching(customer, password)) {
                session.setAttribute("user", customer);
                model.addAttribute("user", customer);
                return customer;
            }
        } catch (UserNotFoundException ignored) {
        }
        try {
            Admin admin = adminService.getAdminByUsername(username);
            if (adminService.passwordsAreMatching(admin, password)) {
                session.setAttribute("user", admin);
                model.addAttribute("user", admin);
                return admin;
            }
        } catch (UserNotFoundException ignored) {
        }
        return null;
    }

    @PostMapping("contact")
    public String sendMessage(@RequestParam("email") String email, @RequestParam("message") String message) {
        try {
            gmailClientService.getSimpleMessage(email, message);
            return "SENT";
        } catch (MessagingException e) {
            return e.getMessage();
        }
    }

    @PostMapping("recover/password")
    public Customer recoverPassword(@RequestParam("pass_email") String email, @RequestParam("username") String username) {
        try {
            Customer customer = customerService.getCustomerByUsername(username);
            if (customer.getCustomerEmail().equals(email)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for password recovery contact us by email",
                        "RESET PASSWORD REQUEST");
            }
            return customer;
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        return null;
    }

    @PostMapping("recover/username")
    public Customer recoverUsername(@RequestParam("pass_email") String email, @RequestParam("password") String password) {
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            if (customerService.passwordsAreMatching(customer, password)) {
                gmailClientService.sendSimpleMessage(customer, "If you asked for username recovery contact us by email",
                        "RESET USERNAME REQUEST");
            }
            return customer;
        } catch (UserNotFoundException | MessagingException ignored) {
        }
        return null;
    }
}
