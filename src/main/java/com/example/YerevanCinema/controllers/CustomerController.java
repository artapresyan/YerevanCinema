package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.services.implementations.CustomerServiceImpl;
import com.example.YerevanCinema.services.implementations.GmailClientServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    private final CustomerServiceImpl customerService;

    private final GmailClientServiceImpl gmailClientService;

    public CustomerController(CustomerServiceImpl customerService, GmailClientServiceImpl gmailClientService) {
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
    }

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
    public String getContactPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("user");
        model.addAttribute("user", customer);
        return "contact_view";
    }

    @PostMapping("contact")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("user");
            gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
        } catch (MessagingException e) {
            return "contact_view";
        }
        return "main_view";
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
