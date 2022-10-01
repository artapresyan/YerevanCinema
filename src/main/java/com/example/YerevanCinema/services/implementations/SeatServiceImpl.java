package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Seat;
import com.example.YerevanCinema.exceptions.SeatAlreadyExistsException;
import com.example.YerevanCinema.exceptions.SeatNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.SeatRepository;
import com.example.YerevanCinema.services.SeatService;
import com.example.YerevanCinema.services.validations.SeatValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatValidationService seatValidationService;
    private final AdminServiceImpl adminServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger();

    public SeatServiceImpl(SeatRepository seatRepository, SeatValidationService seatValidationService,
                           AdminServiceImpl adminServiceImpl, PasswordEncoder passwordEncoder) {
        this.seatRepository = seatRepository;
        this.seatValidationService = seatValidationService;
        this.adminServiceImpl = adminServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Seat getSeatByID(Long seatID) throws SeatNotFoundException {
        Optional<Seat> seat = seatRepository.findById(seatID);
        if (seat.isPresent()) {
            return seat.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get seat by ' %s ' id", seatID));
            throw new SeatNotFoundException("Seat with ' %s ' id not found");
        }
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override
    public Seat addSeat(Long adminID, String password, Hall hall, Integer seatLine, Integer seatNumber, Boolean isSold) {
        try {
            Admin admin = adminServiceImpl.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                seatValidationService.validateSeatCoordinates(hall, seatNumber, seatLine);
                seatValidationService.validateIsSold(isSold);
                Seat seat = new Seat(seatLine, seatNumber, isSold, hall);
                seatRepository.save(seat);
                return seat;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (SeatAlreadyExistsException | IOException | UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to add seat");
            return null;
        }
    }

    @Override
    public Seat removeSeat(Long adminID, String password, Long seatID) {
        try {
            Admin admin = adminServiceImpl.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Seat seat = getSeatByID(seatID);
                seatRepository.deleteById(seatID);
                return seat;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException | SeatNotFoundException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove seat with ' %s ' id", seatID));
            return null;
        }
    }

    @Override
    public Seat updateSeat(Long seatID, Integer seatLine, Integer seatNumber, Boolean isSold) {
        try {
            Seat seat = getSeatByID(seatID);
            try {
                seatValidationService.validateSeatCoordinates(seat.getHall(), seatNumber, seatLine);
                seat.setSeatLine(seatLine);
                seat.setSeatNumber(seatNumber);
            } catch (SeatAlreadyExistsException | IOException ignored) {
            }
            try {
                seatValidationService.validateIsSold(isSold);
                seat.setIsSold(isSold);
            } catch (IOException ignored) {
            }
            seatRepository.save(seat);
            return seat;
        } catch (SeatNotFoundException e) {
            logger.log(Level.ERROR, String.format("Cannot get seat with %s id to update information", seatID));
            return null;
        }
    }
}
