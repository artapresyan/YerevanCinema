package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.NoSuchCustomerException;
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


}
