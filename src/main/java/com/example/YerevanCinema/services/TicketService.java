package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;

public interface TicketService {

    Ticket getTicketByID(Long ticketID) throws TicketNotFoundException;

    Ticket addTicket(Admin admin, String password, Customer customer, MovieSession movieSession);

    Ticket updateTicket(Long ticketID, Customer customer, MovieSession movieSession);

    Ticket removeTicket(Admin admin, String password, Long ticketID);
}
