package com.example.YerevanCinema.exceptions;

public class SessionAlreadyExistsException extends Exception{

    public SessionAlreadyExistsException(String message){
        super(message);
    }
}
