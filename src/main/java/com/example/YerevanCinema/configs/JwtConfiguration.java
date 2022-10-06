package com.example.YerevanCinema.configs;

import com.example.YerevanCinema.services.implementations.JwtTokenServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {

    @Bean
    public JwtTokenServiceImpl jwtTokenServiceImpl(){
        return new JwtTokenServiceImpl();
    }
}
