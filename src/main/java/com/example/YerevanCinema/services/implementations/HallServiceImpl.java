package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.HallRepository;
import com.example.YerevanCinema.services.HallService;
import com.example.YerevanCinema.services.validations.HallValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final Logger logger = LogManager.getLogger();

    public HallServiceImpl(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    public Hall getHallByID(Long hallID) throws HallNotFoundException {
        Optional<Hall> hall = hallRepository.findById(hallID);
        if (hall.isPresent()) {
            return hall.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get hall by ' %s ' id", hallID));
            throw new HallNotFoundException("Hall not found");
        }
    }

    @Override
    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }

    @Override
    public Hall addHall(Long adminID, String password, String hallName, Integer hallCapacity,
                        HallValidationService hallValidationService, AdminServiceImpl adminService,
                        PasswordEncoder passwordEncoder) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                hallValidationService.validateHallName(hallName);
                hallValidationService.validateHallCapacity(hallCapacity);
                Hall hall = new Hall(hallName, hallCapacity);
                hallRepository.save(hall);
                return hall;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException | IOException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to add hall");
            return null;
        }
    }

    @Override
    public Hall removeHall(Long adminID, String password, Long hallID, AdminServiceImpl adminService,
                           PasswordEncoder passwordEncoder) {
        try {
            Admin admin = adminService.getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                Hall hall = getHallByID(hallID);
                hallRepository.deleteById(hallID);
                return hall;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException | HallNotFoundException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to remove" +
                    " hall with ' %s ' id", hallID));
            return null;
        }
    }

    @Override
    public Hall updateHall(Long hallID, String hallName, Integer hallCapacity, HallValidationService hallValidationService) {
        try {
            Hall hall = getHallByID(hallID);
            try {
                hallValidationService.validateHallName(hallName);
                hall.setHallName(hallName);
            } catch (IOException ignored) {
            }
            try {
                hallValidationService.validateHallCapacity(hallCapacity);
                hall.setHallCapacity(hallCapacity);
            } catch (IOException ignored) {
            }
            hallRepository.save(hall);
            return hall;
        } catch (HallNotFoundException e) {
            logger.log(Level.ERROR, String.format("Cannot get hall with %s id to update information", hallID));
            return null;
        }
    }

    @Override
    public Hall getHallByName(String hallName) throws HallNotFoundException {
        Hall hall = hallRepository.getByHallName(hallName);
        if (hall != null) {
            return hall;
        } else {
            logger.log(Level.ERROR, String.format("Cannot get hall with %s name", hallName));
            throw new HallNotFoundException("Hall not found");
        }
    }
}
