package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Seat findBySeatLineAndSeatNumber(Integer seatLine, Integer seatColumn);
}
