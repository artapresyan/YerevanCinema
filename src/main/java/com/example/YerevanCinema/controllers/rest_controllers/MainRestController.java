package com.example.YerevanCinema.controllers.rest_controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/api/")
public class MainRestController {

    private final CustomerServiceImpl customerService;

    public MainRestController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
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
}
