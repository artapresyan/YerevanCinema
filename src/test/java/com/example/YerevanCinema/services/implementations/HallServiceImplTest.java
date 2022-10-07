package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.entities.Admin;
import com.example.YerevanCinema.entities.Hall;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.repositories.AdminRepository;
import com.example.YerevanCinema.repositories.HallRepository;
import com.example.YerevanCinema.services.validations.HallValidationService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class HallServiceImplTest {
    @Mock
    private HallRepository hallRepository;

    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    HallServiceImpl hallService;
    @InjectMocks
    private HallValidationService hallValidationService;
    @InjectMocks
    private AdminServiceImpl adminService;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getHallByIDTest() throws HallNotFoundException {
        Hall expectedHall = new Hall("Red", 80);
        expectedHall.setHallID(5L);

        when(hallRepository.findById(5L)).thenReturn(Optional.of(expectedHall));

        Hall actualHall = hallService.getHallByID(5L);

        assertEquals(expectedHall, actualHall);
    }

    @Test
    public void getAllHallsTest() {
        List<Hall> expectedHalls = getTwentyHalls();

        when(hallRepository.findAll()).thenReturn(expectedHalls);

        List<Hall> actualHalls = hallService.getAllHalls();

        assertTrue(actualHalls.containsAll(expectedHalls));
    }

    @Test
    public void addHallTest() throws UserNotFoundException {
        String decodedPassword = "Artasham345$";
        Admin admin = new Admin();
        admin.setAdminId(8L);
        admin.setAdminUsername("exishe12");
        admin.setAdminPassword(passwordEncoder.encode(decodedPassword));

        when(adminRepository.findById(8L)).thenReturn(Optional.of(admin));


        Admin actualAdmin = adminService.getAdminByID(8L);

        assertEquals(admin.getAdminUsername(), actualAdmin.getAdminUsername());
        assertTrue(passwordEncoder.matches(decodedPassword, actualAdmin.getAdminPassword()));

        Hall expectedHall = new Hall("Big", 150);

        when(hallRepository.save(Mockito.any(Hall.class))).thenReturn(expectedHall);

        Hall actualHall = hallService.addHall(admin.getAdminId(), decodedPassword, expectedHall.getHallName(),
                expectedHall.getHallCapacity(), hallValidationService, adminService, passwordEncoder);

        assertEquals(expectedHall.getHallName(), actualHall.getHallName());
        assertEquals(expectedHall.getHallCapacity(), actualHall.getHallCapacity());

    }

    @Test
    public void removeHallTest() throws UserNotFoundException {
        String decodedPassword = "Artasham345$";
        Admin admin = new Admin();
        admin.setAdminId(8L);
        admin.setAdminUsername("exishe12");
        admin.setAdminPassword(passwordEncoder.encode(decodedPassword));

        when(adminRepository.findById(8L)).thenReturn(Optional.of(admin));


        Admin actualAdmin = adminService.getAdminByID(8L);

        assertEquals(admin.getAdminUsername(), actualAdmin.getAdminUsername());
        assertTrue(passwordEncoder.matches(decodedPassword, actualAdmin.getAdminPassword()));

        Hall expectedHall = new Hall("Big", 150);
        expectedHall.setHallID(15L);

        when(hallRepository.findById(expectedHall.getHallID())).thenReturn(Optional.of(expectedHall));

        Hall actualHall = hallService.removeHall(admin.getAdminId(), decodedPassword,
                expectedHall.getHallID(), adminService, passwordEncoder);

        Mockito.verify(hallRepository).deleteById(expectedHall.getHallID());

        assertEquals(expectedHall, actualHall);
    }

    @Test
    public void updateHallTest() {
        Hall hall = new Hall("Big", 150);
        hall.setHallID(73L);
        Hall expectedHall = new Hall("Blue", 300);
        expectedHall.setHallID(73L);

        when(hallRepository.save(any(Hall.class))).thenReturn(expectedHall);
        when(hallRepository.findById(hall.getHallID())).thenReturn(Optional.of(hall));

        Hall actualHall = hallService.updateHall(hall.getHallID(), expectedHall.getHallName(),
                expectedHall.getHallCapacity(), hallValidationService);

        assertEquals(expectedHall.getHallName(), actualHall.getHallName());
        assertEquals(expectedHall.getHallCapacity(), actualHall.getHallCapacity());
        assertEquals(expectedHall.getHallID(), actualHall.getHallID());
    }

    @Test
    public void getHallByNameTest() throws HallNotFoundException {
        Hall expectedHall = new Hall("Big", 150);
        expectedHall.setHallID(88L);

        when(hallRepository.getByHallName(expectedHall.getHallName())).thenReturn(expectedHall);

        Hall actualHall = hallService.getHallByName(expectedHall.getHallName());

        assertEquals(expectedHall, actualHall);
    }

    private List<Hall> getTwentyHalls() {
        List<Hall> halls = new ArrayList<>();
        String hallName = "Abc";
        int capacity = 60;

        for (int i = 1, suffix = 'a'; i < 21; i++, suffix++) {
            Hall hall = new Hall(hallName + suffix, capacity + 10 * i);
            hall.setHallID((long) i);
            halls.add(hall);
        }
        return halls;
    }
}
