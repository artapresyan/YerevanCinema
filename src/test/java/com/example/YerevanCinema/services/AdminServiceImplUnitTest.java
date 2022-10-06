package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import com.example.YerevanCinema.services.validations.AdminValidationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Before
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

    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminServiceImpl adminService;

    @InjectMocks
    private AdminValidationService userValidationService;

    @Test
    public void getAdminByIDTest() {
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
    }

    @Test
    public void getAllAdminsTest() {
        when(adminRepository.findAll()).thenReturn(ADMINS);

        List<Admin> actualAdmins = adminService.getAllAdmins();

        assertEquals(ADMINS.size(), actualAdmins.size());
        assertTrue(actualAdmins.containsAll(ADMINS));
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

        Admin actualAdmin = adminService.registerAdmin(name, surname, email, username, password, userValidationService);

        assertEquals(expectedAdmin.getAdminUsername(), actualAdmin.getAdminUsername());
        assertEquals(expectedAdmin.getAdminPassword(), actualAdmin.getAdminPassword());
        assertEquals(expectedAdmin.getAdminEmail(), actualAdmin.getAdminEmail());
    }

}
