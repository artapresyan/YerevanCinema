package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Customer getByAdminUsername(String customerUsername);
    Customer getByAdminEmail(String customerEmail);
}
