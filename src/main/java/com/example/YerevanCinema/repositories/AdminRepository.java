package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin getByAdminUsername(String customerUsername);
    Admin getByAdminEmail(String customerEmail);
}
