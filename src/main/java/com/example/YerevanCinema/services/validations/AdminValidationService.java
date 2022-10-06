package com.example.YerevanCinema.services.validations;

import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.services.UserValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AdminValidationService implements UserValidationService {

    private final AdminRepository adminRepository;
    private final Logger logger = LogManager.getLogger();

    public AdminValidationService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void validateName(String name) throws IOException {
        if (name == null || !name.matches("^[A-Z][a-z]{2,20}$")) {
            logger.log(Level.FATAL, String.format("Please be aware, name ' %s ' is invalid ", name));
            throw new IOException();
        }
    }
    @Override
    public void validateSurname(String surname) throws IOException {
        if (surname == null || !surname.matches("^[A-Z][a-z]{2,30}")) {
            logger.log(Level.FATAL, String.format("Please be aware,surname ' %s ' is invalid ", surname));
            throw new IOException();
        }
    }
    @Override
    public void validateAge(Integer age) throws IOException {
        if (age == null || age < 16) {
            logger.log(Level.FATAL, String.format("Please be aware, age ' %s ' is invalid or restricted by our system",
                    age));
            throw new IOException();
        }

    }
    @Override
    public void validateUsername(String username) throws IOException, UsernameExistsException {
        if (username == null || !username.matches("^(?=.{3,}[a-z])[a-z0-9]{8,30}$")) {
            logger.log(Level.FATAL, String.format("Please be aware, username ' %s ' is invalid", username));
            throw new IOException();
        } else if (adminRepository.getByAdminUsername(username) != null) {
            logger.log(Level.ERROR, String.format("Username ' %s ' already exists, try to get another one", username));
            throw new UsernameExistsException(String.format("Username ' %s ' exists", username));
        }
    }
    @Override
    public void validateEmail(String email) throws IOException, RegisteredEmailException {
        if (email == null || !email.matches("^[a-z][a-z0-9-_.]+[a-z0-9]+@[a-z]+\\.[a-z.]{2,}")) {
            logger.log(Level.FATAL, String.format("Please be aware, email ' %s ' is invalid", email));
            throw new IOException();
        } else if (adminRepository.getByAdminEmail(email) != null) {
            logger.log(Level.ERROR, String.format("Email ' %s ' already registered in system, try with another one", email));
            throw new RegisteredEmailException(String.format("Email ' %s ' already registered", email));
        }
    }
    @Override
    public void validatePassword(String password) throws IOException {
        if (password == null || !password
                .matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%#&()/*?])[A-Za-z\\d@$!%#&()/*?]{8,20}$")) {
            logger.log(Level.FATAL, "Please be aware, password must contain at least" +
                    " one special character, one capital letter, one number, and the minimum length is 10 characters");
            throw new IOException();
        }
    }

}
