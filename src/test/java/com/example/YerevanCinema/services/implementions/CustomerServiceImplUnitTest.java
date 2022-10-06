package com.example.YerevanCinema.services.implementions;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.repositories.CustomerRepository;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplUnitTest {

    private static final List<Customer> CUSTOMERS = new ArrayList<>();

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;
    @InjectMocks
    private CustomerValidationService customerValidationService;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getCustomerByIDTest() {
        fillCustomersList();

        List<Customer> actualCustomers = CUSTOMERS.stream().map(expectedCustomer -> {
            Long id = expectedCustomer.getCustomerID();

            when(customerRepository.findById(id)).thenReturn(Optional.of(expectedCustomer));

            try {
                Customer actualCustomer = customerService.getCustomerByID(id);
                assertSame(expectedCustomer, actualCustomer);
                return actualCustomer;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(CUSTOMERS.size(), actualCustomers.size());

        CUSTOMERS.clear();
    }

    @Test
    public void getAllCustomersTest() {
        fillCustomersList();

        when(customerRepository.findAll()).thenReturn(CUSTOMERS);

        List<Customer> actualCustomers = customerService.getAllCustomers();

        assertEquals(CUSTOMERS.size(), actualCustomers.size());
        assertTrue(actualCustomers.containsAll(CUSTOMERS));

        CUSTOMERS.clear();
    }

    @Test
    public void registerCustomerTest() {
        String name = "Artur";
        String surname = "Apresyan";
        Integer age = 28;
        String email = "my.email@gmail.com";
        String username = "artapresyan";
        String password = "Unknown789!";

        Customer expectedCustomer = new Customer(name, surname, age, username, email, password);

        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(expectedCustomer);

        Customer actualCustomer = customerService.registerCustomer(name, surname, age, username, email, password,
                customerValidationService, passwordEncoder);

        assertEquals(expectedCustomer.getCustomerUsername(), actualCustomer.getCustomerUsername());
        assertTrue(passwordEncoder.matches(expectedCustomer.getCustomerPassword(), actualCustomer.getCustomerPassword()));
        assertEquals(expectedCustomer.getCustomerEmail(), actualCustomer.getCustomerEmail());
    }

    @Test
    public void removeCustomerTest() {
        fillCustomersList();

        List<Customer> actualCustomers = CUSTOMERS.stream().map(customer -> {
            String password = customer.getCustomerPassword();
            customer.setCustomerPassword(passwordEncoder.encode(password));

            when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));

            Customer actualCustomer = customerService.selfRemoveCustomer(customer.getCustomerID(), password, passwordEncoder);

            Mockito.verify(customerRepository).deleteById(customer.getCustomerID());

            assertEquals(customer, actualCustomer);

            return actualCustomer;
        }).collect(Collectors.toList());

        assertEquals(CUSTOMERS.size(), actualCustomers.size());

        CUSTOMERS.clear();
    }

    @Test
    public void updateCustomerDataTest() {
        String decodedPassword = "Unknown789!";
        Customer customer = new Customer("Artur", "Apresyan", 56, "artapresyan",
                "my.email@gmail.com", passwordEncoder.encode(decodedPassword));
        customer.setCustomerID(25L);
        Customer expectedCustomer = new Customer("Hakobik", customer.getCustomerSurname(), customer.getCustomerAge(),
                customer.getCustomerEmail(), customer.getCustomerUsername(), "Exegnadzor123)");
        expectedCustomer.setCustomerID(customer.getCustomerID());

        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(expectedCustomer);
        when(customerRepository.findById(customer.getCustomerID())).thenReturn(Optional.of(customer));

        Customer actualCustomer = customerService.updateCustomerData(customer.getCustomerID(), expectedCustomer.getCustomerName(),
                customer.getCustomerSurname(), customer.getCustomerAge(), customer.getCustomerUsername(),
                customer.getCustomerEmail(), decodedPassword, expectedCustomer.getCustomerPassword(),
                customerValidationService, passwordEncoder);

        assertEquals(expectedCustomer.getCustomerName(), actualCustomer.getCustomerName());
        assertTrue(passwordEncoder.matches(expectedCustomer.getCustomerPassword(), actualCustomer.getCustomerPassword()));
    }


    @Test
    public void getAdminByUsernameTest() {
        fillCustomersList();

        List<Customer> actualCustomers = CUSTOMERS.stream().map(expectedCustomer -> {
            String username = expectedCustomer.getCustomerUsername();

            when(customerRepository.getByCustomerUsername(username)).thenReturn(expectedCustomer);

            try {
                Customer actualCustomer = customerService.getCustomerByUsername(username);
                assertSame(expectedCustomer, actualCustomer);
                return actualCustomer;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(CUSTOMERS.size(), actualCustomers.size());

        CUSTOMERS.clear();
    }

    @Test
    public void getAdminByEmailTest() {
        fillCustomersList();

        List<Customer> actualCustomers = CUSTOMERS.stream().map(expectedCustomer -> {
            String email = expectedCustomer.getCustomerEmail();

            when(customerRepository.getByCustomerEmail(email)).thenReturn(expectedCustomer);

            try {
                Customer actualCustomer = customerService.getCustomerByEmail(email);
                assertSame(expectedCustomer, actualCustomer);
                return actualCustomer;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(CUSTOMERS.size(), actualCustomers.size());

        CUSTOMERS.clear();
    }

    public void fillCustomersList() {
        String name = "Artur";
        String surname = "Apresyan";
        String email = "my.email@gmail.com";
        String username = "artapresyan";
        String password = "unknown";
        for (int i = 1, suffix = 'a', prefix = 'z'; i < 21; i++, suffix++, prefix--) {
            Customer customer = new Customer(name + suffix, prefix + surname + suffix,
                    38 + i, prefix + email, suffix + username + prefix,
                    suffix + password + prefix);
            customer.setCustomerID((long) i);
            CUSTOMERS.add(customer);
        }
    }
}
