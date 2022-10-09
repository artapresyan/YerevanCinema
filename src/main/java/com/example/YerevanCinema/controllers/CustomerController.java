package com.example.YerevanCinema.controllers;

import com.example.YerevanCinema.entities.Customer;
import com.example.YerevanCinema.entities.MovieSession;
import com.example.YerevanCinema.entities.Ticket;
import com.example.YerevanCinema.exceptions.HallNotFoundException;
import com.example.YerevanCinema.exceptions.MovieNotFoundException;
import com.example.YerevanCinema.exceptions.TicketNotFoundException;
import com.example.YerevanCinema.exceptions.UserNotFoundException;
import com.example.YerevanCinema.services.implementations.*;
import com.example.YerevanCinema.services.validations.CustomerValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/")
public class CustomerController {

    @Value("${qr.path}")
    private String qrPath;
    private final MovieSessionServiceImpl movieSessionService;
    private final CustomerServiceImpl customerService;
    private final GmailClientServiceImpl gmailClientService;
    private final TicketServiceImpl ticketService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerValidationService customerValidationService;
    private final QRCodeServiceImpl qrCodeService;

    public CustomerController(MovieSessionServiceImpl movieSessionService, CustomerServiceImpl customerService,
                              GmailClientServiceImpl gmailClientService, TicketServiceImpl ticketService,
                              PasswordEncoder passwordEncoder, CustomerValidationService customerValidationService,
                              QRCodeServiceImpl qrCodeService) {
        this.movieSessionService = movieSessionService;
        this.customerService = customerService;
        this.gmailClientService = gmailClientService;
        this.ticketService = ticketService;
        this.passwordEncoder = passwordEncoder;
        this.customerValidationService = customerValidationService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public String getCustomerMainPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        return "customer_main_view";
    }

    @GetMapping("about")
    public String getAboutPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        return "customer_about_view";
    }

    @GetMapping("contact")
    public String getContactPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        return "customer_contact_view";
    }

    @PostMapping("contact_post")
    public String sendMessage(HttpSession session, @RequestParam("message") String message) {
        try {
            Customer customer = (Customer) session.getAttribute("customer");
            gmailClientService.getSimpleMessage(customer.getCustomerEmail(), message);
        } catch (MessagingException e) {
            return "redirect:/customer/contact";
        }
        return "redirect:/customer/";
    }

    @GetMapping("details")
    public String getAccountDetailsPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        return "customer_details_view";
    }

    @PostMapping("details")
    public String removeAccount(HttpSession session, String password) {
        Customer customer = (Customer) session.getAttribute("customer");
        customerService.selfRemoveCustomer(customer.getCustomerID(), password, passwordEncoder);
        return "redirect:/";
    }

    @GetMapping("details/edit")
    public String getAccountDetailsEditPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        return "customer_details_edit_view";
    }

    @PostMapping("details/edit_change")
    public String updateAccountDetails(@RequestParam(value = "customer_name", required = false) String newName,
                                       @RequestParam(value = "customer_surname", required = false) String newSurname,
                                       @RequestParam(value = "customer_age", required = false) Integer newAge,
                                       @RequestParam(value = "customer_username", required = false) String newUsername,
                                       @RequestParam(value = "customer_email", required = false) String newEmail,
                                       @RequestParam(value = "new_password", required = false) String newPassword,
                                       @RequestParam(value = "new_password_confirm", required = false) String confirmNewPassword,
                                       @RequestParam("password") String password, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (newPassword.equals(confirmNewPassword)) {
            customerService.updateCustomerData(customer.getCustomerID(), newName, newSurname, newAge, newUsername,
                    newEmail, password, newPassword, customerValidationService, passwordEncoder);
        }
        try {
            customer = customerService.getCustomerByID(customer.getCustomerID());
            session.setAttribute("customer", customer);
            model.addAttribute("customer", customer);
            return "redirect:/customer/details";
        } catch (UserNotFoundException e) {
            return "redirect:/customer/details/edit";
        }
    }

    @GetMapping("tickets")
    public String getPurchasedTickets(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        List<Ticket> tickets = ticketService.getAllTickets().stream()
                .filter(ticket -> ticket.getCustomer().getCustomerID().equals(customer.getCustomerID()))
                .collect(Collectors.toList());
        model.addAttribute("tickets", tickets);
        return "customer_tickets_view";
    }

    @GetMapping("sessions")
    public String getSessionsPage(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);
        List<MovieSession> sessions = movieSessionService.getAllMovieSessions().stream()
                .filter(movieSession -> LocalDateTime.parse(movieSession.getMovieSessionStart())
                        .isBefore(LocalDateTime.now().plusDays(14)))
                .collect(Collectors.toList());
        model.addAttribute("sessions", sessions);
        return "customer_sessions_view";
    }

    @PostMapping("sessions/selected")
    public String getSessionsByMovieName(@RequestParam("key_value") String keyValue,
                                         @RequestParam("selected") String selected, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        model.addAttribute("customer", customer);

        try {
            List<MovieSession> movieSessions = getSelectedMovieSessions(keyValue,selected);
            model.addAttribute("selected_movie_sessions", movieSessions);
            return "customer_sessions_selected_view";
        } catch (MovieNotFoundException | HallNotFoundException e) {
            return "redirect:/customer/sessions";
        }
    }

    @PostMapping("sessions/purchase")
    public String sendTicketToCustomer(@RequestParam("selected_session_id") Long movieSessionID, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            MovieSession movieSession = movieSessionService.getMovieSessionByID(movieSessionID);
            Ticket ticket = ticketService.addTicket(customer, movieSession);
            qrCodeService.generateQRCodeImage(customer);
            gmailClientService.sendMessageWithAttachment(customer, String.format(qrPath, ticket.getTicketID(),
                    customer.getCustomerUsername()));
            return "redirect:/customer/tickets";
        } catch (Exception e) {
            return "redirect:/customer/sessions";
        }
    }

    @PostMapping("tickets/resend")
    public String resendTicketToCustomer(@RequestParam("ticket_id") Long ticketID, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Ticket ticket = ticketService.getTicketByID(ticketID);
            if (customer.getCustomerID().equals(ticket.getCustomer().getCustomerID())) {
                qrCodeService.generateQRCodeImage(customer);
                gmailClientService.sendMessageWithAttachment(customer, String.format(qrPath, ticket.getTicketID(),
                        customer.getCustomerUsername()));
                return "redirect:/customer/tickets";
            } else
                throw new TicketNotFoundException("No Such Ticket");
        } catch (Exception e) {
            return "redirect:/customer/";
        }
    }

    private List<MovieSession> getSelectedMovieSessions(String keyValue, String selected) throws MovieNotFoundException, HallNotFoundException {
        if (keyValue.equalsIgnoreCase("Movie"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovie().getMovieName().equals(selected))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Category"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovie().getMovieCategory().equals(selected))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Price"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getMovieSessionPrice().equals(Integer.parseInt(selected)))
                    .collect(Collectors.toList());
        else if (keyValue.equalsIgnoreCase("Hall"))
            return movieSessionService.getAllMovieSessions().stream()
                    .filter(movieSession -> movieSession.getHall().getHallName().equals(selected))
                    .collect(Collectors.toList());
        else
            return List.of();
    }
}
