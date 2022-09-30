package com.example.YerevanCinema.exceptions;

public class MovieSessionAlreadyExistsException extends Exception{

    public MovieSessionAlreadyExistsException(String message){
        super(message);
    }
}
