package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;

import javax.mail.MessagingException;

public interface EmailService {

    void sendMessageWithAttachment(Customer customer, String pathToAttachment) throws MessagingException;

    void sendSimpleMessage(Customer customer,String text, String subject) throws MessagingException;
}
