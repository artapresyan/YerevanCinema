package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class GmailClientService implements EmailService{

    private final JavaMailSender emailSender;

    public GmailClientService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendMessageWithAttachment(Customer customer, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(customer.getCustomerEmail());
        helper.setSubject("Ticket");
        helper.setText("Scan QR attached to this mail");

        FileSystemResource file
                = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("QR", file.getFile());
        emailSender.send(message);
    }

    @Override
    public void sendSimpleMessage(Customer customer, String text, String subject) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(customer.getCustomerEmail());
        helper.setSubject(subject);
        helper.setText(text);

        emailSender.send(message);
    }
}
