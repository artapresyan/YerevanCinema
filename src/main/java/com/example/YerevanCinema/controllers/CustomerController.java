package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    @GetMapping
    private String getCustomerHomePage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
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
