package com.example.YerevanCinema.services.detailsServices;

import com.example.YerevanCinema.entities.entityDetails.AdminDetails;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AdminDetailsService implements UserDetailsService {

    private final AdminServiceImpl adminService;

    public AdminDetailsService(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new AdminDetails(adminService.getAdminByUsername(username));
        } catch (UserNotFoundException e) {
            return null;
        }
    }
}
