package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplUnitTest {

    private static final List<Admin> ADMINS = new ArrayList<>();

    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminServiceImpl adminService;
    @InjectMocks
    private AdminValidationService userValidationService;

    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getAdminByIDTest() {
        fillAdminsList();

        List<Admin> actualAdmins = ADMINS.stream().map(expectedAdmin -> {
            Long id = expectedAdmin.getAdminId();

            when(adminRepository.findById(id)).thenReturn(Optional.of(expectedAdmin));

            try {
                Admin actualAdmin = adminService.getAdminByID(id);
                assertSame(expectedAdmin, actualAdmin);
                return actualAdmin;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(ADMINS.size(), actualAdmins.size());

        ADMINS.clear();
    }

    @Test
    public void getAllAdminsTest() {
        fillAdminsList();

        when(adminRepository.findAll()).thenReturn(ADMINS);

        List<Admin> actualAdmins = adminService.getAllAdmins();

        assertEquals(ADMINS.size(), actualAdmins.size());
        assertTrue(actualAdmins.containsAll(ADMINS));

        ADMINS.clear();
    }

    @Test
    public void registerAdminTest() {
        String name = "Artur";
        String surname = "Apresyan";
        String email = "my.email@gmail.com";
        String username = "artapresyan";
        String password = "Unknown789!";

        Admin expectedAdmin = new Admin(name, surname, email, username, password);

        when(adminRepository.save(Mockito.any(Admin.class))).thenReturn(expectedAdmin);

        Admin actualAdmin = adminService.registerAdmin(name, surname, email, username, password, userValidationService
                , passwordEncoder);

        assertEquals(expectedAdmin.getAdminUsername(), actualAdmin.getAdminUsername());
        assertTrue(passwordEncoder.matches(expectedAdmin.getAdminPassword(), actualAdmin.getAdminPassword()));
        assertEquals(expectedAdmin.getAdminEmail(), actualAdmin.getAdminEmail());
    }

    @Test
    public void removeAdminTest() {
        fillAdminsList();

        List<Admin> actualAdmins = ADMINS.stream().map(admin -> {
            String password = admin.getAdminPassword();
            admin.setAdminPassword(passwordEncoder.encode(password));
            when(adminRepository.findById(admin.getAdminId())).thenReturn(Optional.of(admin));

            Admin actualAdmin = adminService.removeAdmin(admin.getAdminId(), password, passwordEncoder);

            Mockito.verify(adminRepository).deleteById(admin.getAdminId());
            assertEquals(admin, actualAdmin);

            return actualAdmin;
        }).collect(Collectors.toList());

        assertEquals(ADMINS.size(), actualAdmins.size());

        ADMINS.clear();
    }

    @Test
    public void updateAdminDataTest() {
        String decodedPassword = "Unknown789!";
        Admin admin = new Admin("Artur", "Apresyan", "my.email@gmail.com",
                "artapresyan", passwordEncoder.encode(decodedPassword));
        admin.setAdminId(25L);
        Admin expectedAdmin = new Admin("Hakobik", admin.getAdminSurname(), admin.getAdminEmail(),
                admin.getAdminUsername(), "Exegnadzor123)");
        expectedAdmin.setAdminId(admin.getAdminId());

        when(adminRepository.save(Mockito.any(Admin.class))).thenReturn(expectedAdmin);
        when(adminRepository.findById(admin.getAdminId())).thenReturn(Optional.of(admin));

        Admin actualAdmin = adminService.updateAdminData(admin.getAdminId(), expectedAdmin.getAdminName(),
                admin.getAdminSurname(), admin.getAdminUsername(), admin.getAdminEmail(), decodedPassword,
                expectedAdmin.getAdminPassword(), userValidationService, passwordEncoder);

        assertEquals(expectedAdmin.getAdminName(), actualAdmin.getAdminName());
        assertTrue(passwordEncoder.matches(expectedAdmin.getAdminPassword(), actualAdmin.getAdminPassword()));
    }

    @Test
    public void getAdminByUsernameTest() {
        fillAdminsList();

        List<Admin> actualAdmins = ADMINS.stream().map(expectedAdmin -> {
            String username = expectedAdmin.getAdminUsername();

            when(adminRepository.getByAdminUsername(username)).thenReturn(expectedAdmin);

            try {
                Admin actualAdmin = adminService.getAdminByUsername(username);
                assertSame(expectedAdmin, actualAdmin);
                return actualAdmin;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(ADMINS.size(), actualAdmins.size());

        ADMINS.clear();
    }

    @Test
    public void getAdminByEmailTest() {
        fillAdminsList();

        List<Admin> actualAdmins = ADMINS.stream().map(expectedAdmin -> {
            String email = expectedAdmin.getAdminEmail();

            when(adminRepository.getByAdminEmail(email)).thenReturn(expectedAdmin);

            try {
                Admin actualAdmin = adminService.getAdminByEmail(email);
                assertSame(expectedAdmin, actualAdmin);
                return actualAdmin;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(ADMINS.size(), actualAdmins.size());

        ADMINS.clear();
    }

    public void fillAdminsList() {
        String name = "Artur";
        String surname = "Apresyan";
        String email = "my.email@gmail.com";
        String username = "artapresyan";
        String password = "unknown";
        for (int i = 1, suffix = 'a', prefix = 'z'; i < 21; i++, suffix++, prefix--) {
            Admin admin = new Admin(name + suffix, prefix + surname + suffix, prefix + email,
                    suffix + username + prefix, suffix + password + prefix);
            admin.setAdminId((long) i);
            ADMINS.add(admin);
        }
    }
}
