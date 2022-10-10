package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;

import java.util.List;

public interface TicketService {

    List<Ticket> getAllTickets();
    Ticket getTicketByID(Long ticketID) throws TicketNotFoundException;

    Ticket addTicket(Customer customer, MovieSession movieSession);

    Ticket updateTicket(Long ticketID, Customer customer, MovieSession movieSession);

    List<Ticket> deleteAllTicketsByCustomerID(Long customerID);
    Ticket removeTicket(Long ticketID);
}
