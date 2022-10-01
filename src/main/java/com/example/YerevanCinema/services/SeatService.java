package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.entities.Seat;
import com.example.YerevanCinema.exceptions.SeatNotFoundException;

import java.util.List;

public interface SeatService {

    Seat getSeatByID(Long seatID) throws SeatNotFoundException;

    List<Seat> getAllSeats();

    Seat addSeat(Long adminID, String password, Hall hall, Integer seatLine, Integer seatNumber, Boolean isSold);

    Seat removeSeat(Long adminID, String password, Long seatID);

    Seat updateSeat(Long seatID, Integer seatLine, Integer seatNumber, Boolean isSold);


}
