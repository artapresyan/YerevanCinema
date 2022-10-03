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
    public String getCustomerHomePage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "main_view";
    }

    @GetMapping("about")
    public String getAboutPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "about_view";
    }

    @GetMapping("contact")
    public String getContactPage(HttpSession session, Model model){
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "contact_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage() {
        return "sessions_view";
    }

    @GetMapping("details")
    public String getDetailsPage() {
        return "details_view";
    }

    @GetMapping("sessions/seat")
    public String getSeatsPage() {
        return "seat_view";
    }
}
