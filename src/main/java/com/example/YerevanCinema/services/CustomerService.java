package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.NoSuchCustomerException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.repositories.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer getCustomerByID(Long customerID) throws NoSuchCustomerException {
        Optional<Customer> customer = customerRepository.findById(customerID);
        if (customer.isPresent()) {
            return customer.get();
        } else
            throw new NoSuchCustomerException(String.format("No customer with %s id", customerID));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }



    private void validateData(String customerName, String customerSurname, Integer customerAge,
                              String customerUsername, String customerEmail, String customerPassword)
            throws UsernameExistsException, NullPointerException, RegisteredEmailException {
        if (customerName == null) {
            throw new NullPointerException("Name is missing, fill it in");
        } else if (customerSurname == null) {
            throw new NullPointerException("Surname is missing, fill it in");
        } else if (customerAge == null) {
            throw new NullPointerException("Age is missing, fill it in");
        } else if (customerUsername == null) {
            throw new NullPointerException("Username is missing, fill it in");
        } else if (customerRepository.getByCustomerUsername(customerUsername) != null) {
            throw new UsernameExistsException("Username already exists. Try to get another one");
        } else if (customerEmail == null) {
            throw new NullPointerException("Email is missing, fill it in");
        } else if (customerRepository.getByCustomerEmail(customerEmail) != null) {
            throw new RegisteredEmailException("Email already registered. Try to get another one");
        } else if (customerPassword == null) {
            throw new NullPointerException("Password is missing, fill it in");
        }
    }
}
