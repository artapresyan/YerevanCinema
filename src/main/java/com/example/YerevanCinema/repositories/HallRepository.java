package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {

    Hall getByHallName(String hallName);
}
