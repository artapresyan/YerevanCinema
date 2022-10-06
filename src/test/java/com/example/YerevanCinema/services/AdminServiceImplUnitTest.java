package com.example.YerevanCinema.services;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.services.implementations.AdminServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplUnitTest {

    private static final List<Admin> ADMINS = new ArrayList<>();

    @Before
    public void fillAdminsList() {
        String name = "Artur";
        for (int i = 1, suffix = 'a'; i < 21; i++, suffix++) {
            Admin admin = new Admin();
            admin.setAdminId((long) i);
            admin.setAdminName(name + suffix);
            AdminServiceImplUnitTest.ADMINS.add(admin);
        }
    }

    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    @Test
    public void getAdminByIDTest() {
        List<Admin> actualAdmins = ADMINS.stream().map(expectedAdmin -> {
            Long id = expectedAdmin.getAdminId();
            when(adminRepository.findById(id)).thenReturn(Optional.of(expectedAdmin));
            try {
               Admin actualAdmin = adminServiceImpl.getAdminByID(id);
                assertSame(expectedAdmin, actualAdmin);
                return actualAdmin;
            } catch (UserNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());

        assertEquals(ADMINS.size(),actualAdmins.size());
    }



}
