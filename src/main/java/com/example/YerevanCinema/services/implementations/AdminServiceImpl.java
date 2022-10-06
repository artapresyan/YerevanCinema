package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.services.AdminService;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final Logger logger = LogManager.getLogger();

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin getAdminByID(Long adminID) throws UserNotFoundException {
        Optional<Admin> admin = adminRepository.findById(adminID);
        if (admin.isPresent()) {
            return admin.get();
        } else {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to get admin by ' %s ' id", adminID));
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin registerAdmin(String adminName, String adminSurname, String adminEmail, String adminUsername,
                               String adminPassword, AdminValidationService userValidationService) {
        try {
            userValidationService.validateName(adminName);
            userValidationService.validateSurname(adminSurname);
            userValidationService.validateUsername(adminUsername);
            userValidationService.validateEmail(adminEmail);
            userValidationService.validatePassword(adminPassword);
            Admin admin = new Admin(adminName, adminSurname, adminEmail, adminUsername, adminPassword);
            adminRepository.save(admin);
            return admin;
        } catch (IOException | UsernameExistsException | RegisteredEmailException e) {
            logger.log(Level.ERROR, "Something went wrong while trying to register admin");
            return null;
        }
    }

    @Override
    public Admin removeAdmin(Long adminID, String password, PasswordEncoder passwordEncoder) {
        try {
            Admin admin = getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                adminRepository.deleteById(adminID);
                return admin;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, String.format("Something went wrong while trying to deactivate" +
                    " admin account with ' %s ' id", adminID));
            return null;
        }
    }

    @Override
    public Admin updateAdminData(Long adminID, String name, String surname, String username, String email,
                                 String password, String newPassword, AdminValidationService userValidationService,
                                 PasswordEncoder passwordEncoder) {
        try {
            Admin admin = getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                try {
                    userValidationService.validateName(name);
                    admin.setAdminName(name);
                } catch (IOException ignored) {
                }
                try {
                    userValidationService.validateSurname(surname);
                    admin.setAdminSurname(surname);
                } catch (IOException ignored) {
                }
                try {
                    userValidationService.validateUsername(username);
                    admin.setAdminUsername(username);
                } catch (IOException | UsernameExistsException ignored) {
                }
                try {
                    userValidationService.validateEmail(email);
                    admin.setAdminEmail(email);
                } catch (IOException | RegisteredEmailException ignored) {
                }
                try {
                    userValidationService.validatePassword(newPassword);
                    admin.setAdminPassword(passwordEncoder.encode(newPassword));
                }catch (IOException ignored){
                }
                adminRepository.save(admin);
                return admin;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (UserNotFoundException | WrongPasswordException e) {
            logger.log(Level.ERROR, String.format("Cannot get admin account with %s id to update information", adminID));
            return null;
        }
    }

    @Override
    public Admin getAdminByUsername(String username) throws UserNotFoundException {
        Admin admin = adminRepository.getByAdminUsername(username);
        if (admin != null)
            return admin;
        else {
            logger.log(Level.ERROR, String.format("Cannot get admin account with %s username", username));
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public Admin getAdminByEmail(String email) throws UserNotFoundException {
        Admin admin = adminRepository.getByAdminEmail(email);
        if (admin != null) {
            return admin;
        } else {
            logger.log(Level.ERROR, String.format("Cannot get admin account with %s email", email));
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public boolean passwordsAreMatching(Admin admin, String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password,admin.getAdminPassword());
    }

    @Override
    public boolean confirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
