package com.example.YerevanCinema.services.detailsServices;

import com.example.YerevanCinema.entities.entityDetails.CustomerDetails;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerServiceImpl customerService;

    public CustomerDetailsService(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new CustomerDetails(customerService.getCustomerByUsername(username));
        } catch (UserNotFoundException e) {
            return null;
        }
    }
}
