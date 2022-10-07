package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.validations.HallValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface HallService {

    Hall getHallByID(Long hallID) throws HallNotFoundException;

    List<Hall> getAllHalls();

    Hall addHall(Long adminID, String password, String hallName, Integer hallCapacity,
                 HallValidationService hallValidationService, AdminServiceImpl adminService,
                 PasswordEncoder passwordEncoder);

    Hall removeHall(Long adminID, String password, Long hallID, AdminServiceImpl adminService, PasswordEncoder passwordEncoder);

    Hall updateHall(Long hallID, String hallName, Integer hallCapacity, HallValidationService hallValidationService);

    Hall getHallByName(String hallName) throws HallNotFoundException;
}
