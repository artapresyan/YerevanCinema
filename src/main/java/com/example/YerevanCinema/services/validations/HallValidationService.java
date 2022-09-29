package com.example.YerevanCinema.services.validations;

import com.example.YerevanCinema.repositories.HallRepository;
import com.example.YerevanCinema.services.AdminService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HallValidationService {

    private final HallRepository hallRepository;
    private final Logger logger = LogManager.getLogger();

    public HallValidationService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public void validateHallName(String hallName) throws IOException {
        if (hallName == null || hallName.length() == 0) {
            logger.log(Level.FATAL, String.format("' %s ' is invalid hall name", hallName));
            throw new IOException();
        } else if (hallRepository.getByHallName(hallName) != null) {
            logger.log(Level.ERROR, String.format("Hall with ' %s ' name already exists", hallName));
            throw new IOException();
        }
    }

    public void validateHallCapacity(Integer hallCapacity) throws IOException{
        if (hallCapacity == null || hallCapacity < 40){
            logger.log(Level.ERROR,String.format("YerevanCinema's smallest hall's capacity is 40, you entered %s",
                    hallCapacity));
            throw new IOException();
        }
    }
}
