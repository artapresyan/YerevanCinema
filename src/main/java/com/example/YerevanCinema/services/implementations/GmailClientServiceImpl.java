package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.services.EmailService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class GmailClientServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public GmailClientServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public MimeMessage sendMessageWithAttachment(Customer customer, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(customer.getCustomerEmail());
        helper.setSubject("Ticket");
        helper.setText("Scan QR attached to this mail");

        FileSystemResource file
                = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("QR", file.getFile());
        emailSender.send(message);
        return message;
    }

    @Override
    public MimeMessage sendSimpleMessage(Customer customer, String text, String subject) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(customer.getCustomerEmail());
        helper.setSubject(subject);
        helper.setText(text);
        emailSender.send(message);
        return message;
    }

    @Override
    public MimeMessage getSimpleMessage(String email, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("cinemayerevan@gmail.com");
        helper.setSubject("ISSUE from " + email);
        helper.setText(text);
        emailSender.send(message);
        return message;
    }
}
