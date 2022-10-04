package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;

import java.util.List;

public interface CustomerService {

    Customer getCustomerByID(Long customerID) throws UserNotFoundException;

    List<Customer> getAllCustomers();

    Customer registerCustomer(String customerName, String customerSurname, Integer customerAge,
                              String customerUsername, String customerEmail, String customerPassword);

    Customer removeCustomer(Long customerID, String password);

    Customer updateCustomerData(Long customerID, String name, String surname, Integer age,
                                String username, String email, String password, String newPassword);

    Customer getCustomerByUsername(String username) throws UserNotFoundException;

    Customer getCustomerByEmail(String email) throws UserNotFoundException;

    boolean passwordsAreMatching(Customer customer, String password);

    boolean confirmPassword(String password, String confirmPassword);
}
