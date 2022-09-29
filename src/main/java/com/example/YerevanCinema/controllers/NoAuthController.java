package com.example.YerevanCinema.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class NoAuthController {

    @GetMapping
    public String getMainPage(){
        return "noAuthMainView";
    }

    @GetMapping("login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("signup")
    public String getSighUpPage(){
        return "signup";
    }

    @GetMapping("about")
    public String getAboutPage(){
        return "noAuthAboutView";
    }

    @GetMapping("contact")
    public String getContactPage(){
        return "noAuthContactView";
    }

    @GetMapping("sessions")
    public String getSessions(){

        return "noAuthSessionsView";
    }
}
