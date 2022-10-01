package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Seat;
import com.example.YerevanCinema.exceptions.HallNotFoundException;

import java.util.List;
import java.util.Set;

public interface HallService {

    Hall getHallByID(Long hallID) throws HallNotFoundException;

    List<Hall> getAllHalls();

    Hall addHall(Long adminID, String password, String hallName, Integer hallCapacity, Set<Seat> seats);

    Hall removeHall(Long adminID, String password, Long hallID);

    Hall updateHall(Long hallID, String hallName, Integer hallCapacity, Set<Seat> seats);

    Hall getHallByName(String hallName) throws HallNotFoundException;
}
