package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Customer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

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
}
