package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.CustomerRepository;
import com.example.YerevanCinema.services.CustomerService;
import com.example.YerevanCinema.services.validations.UserValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService validationService;
    private final Logger logger = LogManager.getLogger();

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                               UserValidationService validationService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    @Override
    public Customer getCustomerByID(Long customerID) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(customerID);
        if (customer.isPresent()) {
            return customer.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get customer by ' %s ' id", customerID));
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
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
            logger.log(Level.ERROR, "Something went wrong while trying to register customer");
            return null;
        }
        Customer customer = new Customer(customerName, customerSurname, customerAge, customerUsername,
                customerEmail, passwordEncoder.encode(customerPassword));
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public Customer removeCustomer(Long customerID, String password) {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                customerRepository.deleteById(customerID);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to deactivate" +
                    " customer account with ' %s ' id", customerID));
            return null;
        }
    }

    @Override
    public Customer updateCustomerData(Long customerID, String name, String surname, Integer age,
                                       String username, String email, String password) {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                try {
                    validationService.validateName(name);
                    customer.setCustomerName(name);
                } catch (IOException ignored) {
                }
                try {
                    validationService.validateSurname(surname);
                    customer.setCustomerSurname(surname);
                } catch (IOException ignored) {
                }
                try {
                    validationService.validateAge(age);
                    customer.setCustomerAge(age);
                } catch (IOException ignored) {
                }
                try {
                    validationService.validateUsername(username);
                    customer.setCustomerUsername(username);
                } catch (IOException | UsernameExistsException ignored) {
                }
                try {
                    validationService.validateEmail(email);
                    customer.setCustomerEmail(email);
                } catch (IOException | RegisteredEmailException ignored) {
                }
                customerRepository.save(customer);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, String.format("Cannot get customer account with %s id to update information", customerID));
            return null;
        }
    }

    @Override
    public Customer getCustomerByUsername(String username) throws UserNotFoundException {
        Customer customer = customerRepository.getByCustomerUsername(username);
        if (customer != null)
            return customer;
        else {
            logger.log(Level.ERROR, String.format("Cannot get customer account with %s username", username));
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public Customer getCustomerByEmail(String email) throws UserNotFoundException {
        Customer customer = customerRepository.getByCustomerEmail(email);
        if (customer != null)
            return customer;
        else {
            logger.log(Level.ERROR, String.format("Cannot get customer account with %s email", email));
            throw new UserNotFoundException("User not found");
        }
    }
}
