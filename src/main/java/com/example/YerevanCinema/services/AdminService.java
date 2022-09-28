package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.NoSuchUserException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.AdminRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final Logger logger = LogManager.getLogger();

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                        ValidationService validationService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
    }

    public Admin getAdminByID(Long adminID) throws NoSuchUserException {
        Optional<Admin> admin = adminRepository.findById(adminID);
        if (admin.isPresent()) {
            return admin.get();
        } else
            throw new NoSuchUserException(String.format("No admin with %s id", admin));
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin registerAdmin(String adminName, String adminSurname, String adminEmail, String adminUsername,
                               String adminPassword) {
            try {
                validationService.validateName(adminName);
                validationService.validateSurname(adminSurname);
                validationService.validateUsername(adminUsername);
                validationService.validateEmail(adminEmail);
                validationService.validatePassword(adminPassword);
            } catch (IOException | UsernameExistsException | RegisteredEmailException e) {
                return null;
            }
            Admin admin = new Admin(adminName, adminSurname, adminEmail, adminUsername, adminPassword);
            adminRepository.save(admin);
            return admin;
    }
    public Admin removeAdmin(Long adminID, String password)  {
        try {
            Admin admin = getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                adminRepository.deleteById(adminID);
                return admin;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException e) {
            logger.log(Level.FATAL,e.getMessage());
        }
        return null;
    }

    public Admin updateAdminData(Long adminID, String name, String surname, String username,
                                    String email, String password) {
        try {
            Admin admin = getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                try{
                    validationService.validateName(name);
                    admin.setAdminName(name);
                }catch (IOException ignored){
                }
                try {
                    validationService.validateSurname(surname);
                    admin.setAdminSurname(surname);
                }catch (IOException ignored){
                }
                try {
                    validationService.validateUsername(username);
                    admin.setAdminUsername(username);
                }catch (IOException | UsernameExistsException ignored){
                }
                try {
                    validationService.validateEmail(email);
                    admin.setAdminEmail(email);
                }catch (IOException | RegisteredEmailException ignored){
                }
                adminRepository.save(admin);
                return admin;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException | WrongPasswordException e) {
            logger.log(Level.FATAL,e.getMessage());
        }
        return null;
    }

    public Admin getAdminByUsername(String username) throws NoSuchUserException {
        Admin admin = adminRepository.getByAdminUsername(username);
        if (admin != null)
            return admin;
        else
            throw new NoSuchUserException(String.format("No admin with %s username", username));
    }

    public Admin getAdminByEmail(String email) throws NoSuchUserException {
        Admin admin = adminRepository.getByAdminEmail(email);
        if (admin != null)
            return admin;
        else throw new NoSuchUserException(String.format("No admin registered with %s email", email));
    }

}
