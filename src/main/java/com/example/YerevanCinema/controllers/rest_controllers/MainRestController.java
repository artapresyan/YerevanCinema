package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;

    public MainRestController(CustomerServiceImpl customerService, AdminServiceImpl adminService) {
        this.customerService = customerService;
        this.adminService = adminService;
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
}
