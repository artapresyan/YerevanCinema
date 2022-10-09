package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;

public interface QRCodeService {

    void generateQRCodeImage(Customer customer, Long ticketID, MovieSession movieSession) throws Exception;
}
