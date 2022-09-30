package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long adminId;

    @Column(name = "name")
    @NotBlank
    private String adminName;

    @Column(name = "surname")
    @NotBlank
    private String adminSurname;

    @Column(name = "email")
    @NotBlank
    private String adminEmail;

    @Column(name = "username")
    @NotBlank
    private String adminUsername;

    @Column(name = "password")
    @NotBlank
    private String adminPassword;

    @OneToMany(mappedBy = "admin")
    private List<MovieSession> addedMovieSessions;

    public Admin(String adminName, String adminSurname, String adminEmail, String adminUsername, String adminPassword) {
        this.adminName = adminName;
        this.adminSurname = adminSurname;
        this.adminEmail = adminEmail;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }
}
