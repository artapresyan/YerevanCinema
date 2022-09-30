package com.example.YerevanCinema.services.validations;

import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Seat;
import com.example.YerevanCinema.exceptions.SeatAlreadyExistsException;
import com.example.YerevanCinema.repositories.SeatRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SeatValidation {

    private final SeatRepository seatRepository;
    private final Logger logger = LogManager.getLogger();

    public SeatValidation(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public void validateSeatCoordinates(Hall hall, Integer number, Integer line) throws IOException, SeatAlreadyExistsException {
        if (number == null || line == null || number < 1 || line < 1) {
            logger.log(Level.FATAL, String.format("Seat line %1$s and number %2$s is invalid", line, number));
            throw new IOException();
        }
        Seat seat = hall.getHallSeats().stream()
                .filter(listSeat -> listSeat.getSeatLine().equals(line) && listSeat.getSeatNumber().equals(number))
                .findAny().orElse(null);
        if (seat != null) {
            logger.log(Level.ERROR, "Seat with line %1$s and %2$s number already exists");
            throw new SeatAlreadyExistsException("Seat exists");
        }
    }
}
