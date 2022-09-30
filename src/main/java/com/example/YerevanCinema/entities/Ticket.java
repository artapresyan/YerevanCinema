package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long ticketID;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "session_id", nullable = false)
    private MovieSession movieSession;


    public Ticket(Customer customer, MovieSession movieSession) {
        this.customer = customer;
        this.movieSession = movieSession;
    }
}
