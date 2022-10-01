package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.TicketRepository;
import com.example.YerevanCinema.services.TicketService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger();

    public TicketServiceImpl(TicketRepository ticketRepository, PasswordEncoder passwordEncoder) {
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Ticket addTicket(Admin admin, String password, Customer customer, MovieSession movieSession) {
        try {
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Ticket ticket = new Ticket(customer, movieSession);
                ticketRepository.save(ticket);
                return ticket;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (WrongPasswordException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to add ticket");
            return null;
        }

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
    public Ticket removeTicket(Admin admin, String password, Long ticketID) {
        try {
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Ticket ticket = getTicketByID(ticketID);
                ticketRepository.deleteById(ticketID);
                return ticket;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (WrongPasswordException | TicketNotFoundException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove" +
                    " ticket with ' %s ' id", ticketID));
            return null;
        }
    }
}
