package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long customerID;


    @Column(name = "name")
    @NotBlank
    private String customerName;

    @Column(name = "surname")
    @NotBlank
    private String customerSurname;

    @Column(name = "age")
    @NotNull
    private Integer customerAge;

    @Column(name = "username", unique = true)
    @NotBlank
    private String customerUsername;

    @Column(name = "email", unique = true)
    @NotBlank
    private String customerEmail;

    @Column(name = "password")
    @NotBlank
    private String customerPassword;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    private List<Ticket> customerTickets;

    public Customer(String customerName, String customerSurname,
                    Integer customerAge, String customerUsername, String customerEmail, String customerPassword) {
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.customerAge = customerAge;
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.customerPassword = customerPassword;
    }
}
