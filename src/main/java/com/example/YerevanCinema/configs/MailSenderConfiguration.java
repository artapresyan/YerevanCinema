package com.example.YerevanCinema.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfiguration {

    @Value("${mail.sender.host}")
    private String MAIL_SENDER_HOST;
    @Value("${mail.sender.port.no.ssl}")
    private Integer MAIL_SENDER_NO_SSL_PORT;
    @Value("${mail.sender.username}")
    private String MAIL_SENDER_USERNAME;
    @Value("${mail.sender.password}")
    private String MAIL_SENDER_PASSWORD;

    @Bean
    JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(MAIL_SENDER_HOST);
        mailSender.setPort(MAIL_SENDER_NO_SSL_PORT);

        mailSender.setUsername(MAIL_SENDER_USERNAME);
        mailSender.setPassword(MAIL_SENDER_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
