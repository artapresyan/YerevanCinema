package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "seats")
@Getter @Setter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long seatID;

    @Column(name = "line")
    @NotNull
    private Integer seatLine;

    @Column(name = "number")
    @NotNull
    private Integer seatNumber;

    @Column(name = "sold")
    @NotNull
    private Boolean isSold;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    public Seat(Integer seatLine, Integer seatNumber, Boolean isSold, Hall hall) {
        this.seatLine = seatLine;
        this.seatNumber = seatNumber;
        this.isSold = isSold;
    }
}
