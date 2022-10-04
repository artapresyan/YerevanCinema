package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.UserNotFoundException;

import java.util.List;

public interface AdminService {

    Admin getAdminByID(Long adminID) throws UserNotFoundException;

    List<Admin> getAllAdmins();

    Admin registerAdmin(String adminName, String adminSurname, String adminEmail, String adminUsername,
                        String adminPassword);

    Admin removeAdmin(Long adminID, String password);

    Admin updateAdminData(Long adminID, String name, String surname, String username,
                          String email, String password, String newPassword);

    Admin getAdminByUsername(String username) throws UserNotFoundException;

    Admin getAdminByEmail(String email) throws UserNotFoundException;

    boolean passwordsAreMatching(Admin admin, String password);

    boolean confirmPassword(String password, String confirmPassword);
}
