package com.example.YerevanCinema.entities.entityDetails;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerDetails implements UserDetails {

    private final Customer customer;

    public CustomerDetails(Customer customer){
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserRole.CUSTOMER.getAuthorities();
    }

    @Override
    public String getPassword() {
        return customer.getCustomerPassword();
    }

    @Override
    public String getUsername() {
        return customer.getCustomerUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
