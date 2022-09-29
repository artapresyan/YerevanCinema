package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.NoSuchUserException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.CustomerRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService validationService;
    private final Logger logger = LogManager.getLogger();

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                           UserValidationService validationService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    public Customer getCustomerByID(Long customerID) throws NoSuchUserException {
        Optional<Customer> customer = customerRepository.findById(customerID);
        if (customer.isPresent()) {
            return customer.get();
        } else
            throw new NoSuchUserException("User cannot be found");
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer registerCustomer(String customerName, String customerSurname, Integer customerAge,
                                     String customerUsername, String customerEmail, String customerPassword) {
        try {
            validationService.validateName(customerName);
            validationService.validateSurname(customerSurname);
            validationService.validateAge(customerAge);
            validationService.validateUsername(customerUsername);
            validationService.validateEmail(customerEmail);
            validationService.validatePassword(customerPassword);
        } catch (IOException | UsernameExistsException | RegisteredEmailException e) {
            return null;
        }
        Customer customer = new Customer(customerName, customerSurname, customerAge, customerUsername,
                customerEmail, passwordEncoder.encode(customerPassword));
        customerRepository.save(customer);
        return customer;
    }

    public Customer removeCustomer(Long customerID, String password) {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                customerRepository.deleteById(customerID);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException e) {
            logger.log(Level.FATAL,e.getMessage());
            return null;
        }
    }

    public Customer updateCustomerData(Long customerID, String name, String surname, Integer age,
                                       String username, String email, String password) {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                try{
                    validationService.validateName(name);
                    customer.setCustomerName(name);
                }catch (IOException ignored){
                }
                try {
                    validationService.validateSurname(surname);
                    customer.setCustomerSurname(surname);
                }catch (IOException ignored){
                }
                try {
                    validationService.validateAge(age);
                    customer.setCustomerAge(age);
                }catch (IOException ignored){
                }
                try {
                    validationService.validateUsername(username);
                    customer.setCustomerUsername(username);
                }catch (IOException | UsernameExistsException ignored){
                }
                try {
                    validationService.validateEmail(email);
                    customer.setCustomerEmail(email);
                }catch (IOException | RegisteredEmailException ignored){
                }
                customerRepository.save(customer);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException e) {
            logger.log(Level.FATAL,e.getMessage());
            return null;
        }
    }

    public Customer getCustomerByUsername(String username) throws NoSuchUserException {
        Customer customer = customerRepository.getByCustomerUsername(username);
        if (customer != null)
            return customer;
        else throw new NoSuchUserException(String.format("No customer with %s username", username));
    }

    public Customer getCustomerByEmail(String email) throws NoSuchUserException {
        Customer customer = customerRepository.getByCustomerEmail(email);
        if (customer != null)
            return customer;
        else
            throw new NoSuchUserException(String.format("No customer registered with %s email", email));
    }

}
