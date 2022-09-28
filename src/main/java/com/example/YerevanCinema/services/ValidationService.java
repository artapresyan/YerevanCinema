package com.example.YerevanCinema.services;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public boolean isValidName(String name) {
        return name != null && name.matches("^[A-Z][a-z]{2,20}$");
    }

    public boolean isValidSurname(String surname) {
        return surname != null && surname.matches("^[A-Z][a-z]{2,30}");
    }

    public boolean isValidAge(Integer age) {
        return age != null && age > 15;
    }

    public boolean isValidUsername(String username){
        return username != null && username.matches("^(?=.{3,}[a-z])[a-z0-9]{4,30}$");
    }

    public boolean isValidEmail(String email){
        return email != null && email.matches("^[a-z][a-z0-9-_.]+[a-z0-9]+@[a-z]+\\.[a-z.]{2,}");
    }

    public boolean isValidPassword(String password){
        return password != null
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$");
    }

}
