package com.example.YerevanCinema.services;

public interface JwtTokenService {

    String getCustomerJwtToken(String email);

    String getAdminJwtToken(String email);
}
