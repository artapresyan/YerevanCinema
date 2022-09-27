package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
