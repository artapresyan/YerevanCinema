package com.example.YerevanCinema.entities;

import javax.persistence.*;

@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long customerID;

    @Column(name = "name", nullable = false)
    private String customerName;

    @Column(name = "surname", nullable = false)
    private String customerSurname;

    @Column(name = "age", nullable = false)
    private Integer customerAge;

    @Column(name = "username",unique = true, nullable = false)
    private String customerUsername;

    @Column(name = "email",unique = true, nullable = false)
    private String customerEmail;

    @Column(name = "password", nullable = false)
    private String customerPassword;

    public Customer() {
    }

    public Customer(String customerName, String customerSurname,
                    Integer customerAge, String customerUsername, String customerEmail, String customerPassword) {
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.customerAge = customerAge;
        this.customerUsername = customerUsername;
        this.customerEmail = customerEmail;
        this.customerPassword = customerPassword;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }

    public void setCustomerSurname(String customerSurname) {
        this.customerSurname = customerSurname;
    }

    public Integer getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(Integer customerAge) {
        this.customerAge = customerAge;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }
}
