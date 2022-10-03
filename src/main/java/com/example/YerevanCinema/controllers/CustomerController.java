package com.example.YerevanCinema.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    @GetMapping("home")
    private String getCustomerHomePage() {
        return "main_view";
    }

    @GetMapping("about")
    private String getAboutPage() {
        return "about_view";
    }

    @GetMapping("sessions")
    private String getSessionsPage() {
        return "about_view";
    }

    @GetMapping("details")
    private String getDetailsPage() {
        return "details_view";
    }

    @GetMapping("sessions/seat")
    private String getSeatsPage() {
        return "about_view";
    }
}
