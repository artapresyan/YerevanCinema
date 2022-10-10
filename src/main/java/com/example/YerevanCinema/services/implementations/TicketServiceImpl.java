package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;
import com.example.YerevanCinema.repositories.TicketRepository;
import com.example.YerevanCinema.services.TicketService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final Logger logger = LogManager.getLogger();

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketByID(Long ticketID) throws TicketNotFoundException {
        Optional<Ticket> ticket = ticketRepository.findById(ticketID);
        if (ticket.isPresent()) {
            return ticket.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get ticket by ' %s ' id", ticketID));
            throw new TicketNotFoundException("Ticket not found");
        }
    }

    @Override
    public Ticket addTicket(Customer customer, MovieSession movieSession) {
        Ticket ticket = new Ticket(customer, movieSession);
        ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public Ticket updateTicket(Long ticketID, Customer customer, MovieSession movieSession) {
        try {
            Ticket ticket = getTicketByID(ticketID);
            ticket.setCustomer(customer);
            ticket.setMovieSession(movieSession);
            ticketRepository.save(ticket);
            return ticket;
        } catch (TicketNotFoundException e) {
            logger.log(Level.ERROR, String.format("Cannot get ticket with %s id to update information", ticketID));
            return null;
        }
    }

    @Override
    public List<Ticket> deleteAllTicketsByCustomerID(Long customerID) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCustomer().getCustomerID().equals(customerID))
                .peek( ticket -> ticketRepository.deleteById(ticket.getTicketID())).collect(Collectors.toList());
    }

    @Override
    public List<Ticket> deleteAllTicketsBySessionID(Long sessionID) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getMovieSession().getMovieSessionID().equals(sessionID))
                .peek( ticket -> ticketRepository.deleteById(ticket.getTicketID())).collect(Collectors.toList());
    }

    @Override
    public Ticket removeTicket(Long ticketID) {
        try {
            Ticket ticket = getTicketByID(ticketID);
            ticketRepository.deleteById(ticketID);
            return ticket;
        } catch (TicketNotFoundException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove" +
                    " ticket with ' %s ' id", ticketID));
            return null;
        }
    }
}
