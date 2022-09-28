package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admins")
@Getter @Setter
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long adminId;

    @Column(name = "name")
    private String adminName;

    @Column(name = "surname")
    private String adminSurname;

    @Column(name = "email")
    private String adminEmail;

    @Column(name = "username")
    private String adminUsername;

    @Column(name = "password")
    private String adminPassword;

    public Admin(String adminName, String adminSurname, String adminEmail, String adminUsername, String adminPassword) {
        this.adminName = adminName;
        this.adminSurname = adminSurname;
        this.adminEmail = adminEmail;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }
}
