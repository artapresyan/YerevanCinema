package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.NoSuchUserException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
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

    public Customer getCustomerByID(Long customerID) throws NoSuchUserException {
        Optional<Customer> customer = customerRepository.findById(customerID);
        if (customer.isPresent()) {
            return customer.get();
        } else
            throw new NoSuchUserException(String.format("No customer with %s id", customerID));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer registerCustomer(String customerName, String customerSurname, Integer customerAge,
                                     String customerUsername, String customerEmail, String customerPassword) {
        try {
            validateData(customerName, customerSurname, customerAge, customerUsername, customerEmail, customerPassword);
            Customer customer = new Customer(customerName, customerSurname, customerAge, customerUsername,
                    customerEmail, passwordEncoder.encode(customerPassword));
            customerRepository.save(customer);
            return customer;
        } catch (UsernameExistsException | RegisteredEmailException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer removeCustomer(Long customerID, String password) throws WrongPasswordException {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                customerRepository.deleteById(customerID);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer updateCustomerData(Long customerID, String name, String surname, Integer age,
                                       String username, String email, String password)
            throws RegisteredEmailException, UsernameExistsException, WrongPasswordException {
        try {
            Customer customer = getCustomerByID(customerID);
            if (passwordEncoder.matches(password, customer.getCustomerPassword())) {
                if (name != null)
                    customer.setCustomerName(name);
                if (surname != null)
                    customer.setCustomerSurname(surname);
                if (age != null)
                    customer.setCustomerAge(age);
                if (username != null) {
                    if (customerRepository.getByCustomerUsername(username) == null)
                        customer.setCustomerUsername(username);
                    else
                        throw new UsernameExistsException("Username already exists. Try to get another one");
                }
                if (email != null) {
                    if (customerRepository.getByCustomerEmail(email) == null)
                        customer.setCustomerEmail(email);
                    else throw new RegisteredEmailException("Email already registered. Try to get another one");
                }
                customerRepository.save(customer);
                return customer;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException e) {
            e.printStackTrace();
        }
        return null;
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
        else throw new NoSuchUserException(String.format("No customer registered with %s email", email));
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
