package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface CustomerService {

    Customer getCustomerByID(Long customerID) throws UserNotFoundException;

    List<Customer> getAllCustomers();

    Customer registerCustomer(String customerName, String customerSurname, Integer customerAge, String customerUsername,
                              String customerEmail, String customerPassword, CustomerValidationService customerValidationService
            , PasswordEncoder passwordEncoder);

    Customer selfRemoveCustomer(Long customerID, String password, PasswordEncoder passwordEncoder);

    Customer adminRemoveCustomer(Long customerID, Admin admin, String password, PasswordEncoder passwordEncoder);

    Customer updateCustomerData(Long customerID, String name, String surname, Integer age,
                                String username, String email, String password, String newPassword,
                                CustomerValidationService customerValidationService, PasswordEncoder passwordEncoder);

    Customer getCustomerByUsername(String username) throws UserNotFoundException;

    Customer getCustomerByEmail(String email) throws UserNotFoundException;

    boolean passwordsAreMatching(Customer customer, String password, PasswordEncoder passwordEncoder);

    boolean confirmPassword(String password, String confirmPassword);
}
