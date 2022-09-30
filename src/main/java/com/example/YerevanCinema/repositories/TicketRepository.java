package com.example.YerevanCinema.repositories;

import com.example.YerevanCinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
