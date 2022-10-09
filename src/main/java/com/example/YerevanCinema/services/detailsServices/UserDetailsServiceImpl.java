package com.example.YerevanCinema.services.detailsServices;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.entityDetails.AdminDetails;
import com.example.YerevanCinema.entities.entityDetails.CustomerDetails;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerServiceImpl customerService;
    private final AdminServiceImpl adminService;

    public UserDetailsServiceImpl(CustomerServiceImpl customerService, AdminServiceImpl adminService) {
        this.customerService = customerService;
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            Customer customer = customerService.getCustomerByUsername(username);
            return new CustomerDetails(customer);
        } catch (UserNotFoundException ignored) {
        }

        try {
            Admin admin = adminService.getAdminByUsername(username);
            return new AdminDetails(admin);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
