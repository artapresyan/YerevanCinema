package com.example.YerevanCinema.services;

import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;

import java.io.IOException;

public interface UserValidationService {

    void validateName(String name) throws IOException;

    void validateSurname(String surname) throws IOException;

    void validateAge(Integer age) throws IOException;

    void validateUsername(String username) throws IOException, UsernameExistsException;

    void validateEmail(String email) throws IOException, RegisteredEmailException;

    void validatePassword(String password) throws IOException;
}
