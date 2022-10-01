package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;

public interface QRCodeService {

    void generateQRCodeImage(Customer customer) throws Exception;
}
