package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.exceptions.NoSuchUserException;
import com.example.YerevanCinema.exceptions.RegisteredEmailException;
import com.example.YerevanCinema.exceptions.UsernameExistsException;
import com.example.YerevanCinema.exceptions.WrongPasswordException;
import com.example.YerevanCinema.repositories.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
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
            validateData(adminName, adminSurname, adminEmail, adminUsername, adminPassword);
            Admin admin = new Admin(adminName, adminSurname, adminEmail, adminUsername, adminPassword);
            adminRepository.save(admin);
            return admin;
        } catch (NullPointerException | UsernameExistsException | RegisteredEmailException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Admin removeCustomer(Long adminID, String password) throws WrongPasswordException {
        try {
            Admin admin = getAdminByID(adminID);
            if (passwordEncoder.matches(password, admin.getAdminPassword())) {
                adminRepository.deleteById(adminID);
                return admin;
            } else
                throw new WrongPasswordException("Entered wrong password");
        } catch (NoSuchUserException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void validateData(String adminName, String adminSurname, String adminEmail, String adminUsername,
                              String adminPassword)
            throws UsernameExistsException, NullPointerException, RegisteredEmailException {
        if (adminName == null) {
            throw new NullPointerException("Name is missing, fill it in");
        } else if (adminSurname == null) {
            throw new NullPointerException("Surname is missing, fill it in");
        } else if (adminUsername == null) {
            throw new NullPointerException("Username is missing, fill it in");
        } else if (adminRepository.getByAdminUsername(adminUsername) != null) {
            throw new UsernameExistsException("Username already exists. Try to get another one");
        } else if (adminEmail == null) {
            throw new NullPointerException("Email is missing, fill it in");
        } else if (adminRepository.getByAdminEmail(adminEmail) != null) {
            throw new RegisteredEmailException("Email already registered. Try to get another one");
        } else if (adminPassword == null) {
            throw new NullPointerException("Password is missing, fill it in");
        }
    }
}
