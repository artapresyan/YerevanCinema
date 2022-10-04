package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface EmailService {

    MimeMessage sendMessageWithAttachment(Customer customer, String pathToAttachment) throws MessagingException;

    MimeMessage sendSimpleMessage(Customer customer,String text, String subject) throws MessagingException;

    MimeMessage getSimpleMessage(String email, String text) throws MessagingException;
}
