package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "halls")
@Getter @Setter
@NoArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long hallID;

    @Column(name = "name", unique = true)
    @NotBlank
    private String hallName;

    @Column(name = "capacity")
    @NotNull
    private Integer hallCapacity;

    @OneToMany(mappedBy = "hall")
    private List<Seat> hallSeats;

    @OneToMany(mappedBy = "hall")
    private List<MovieSession> movieSessions;

    public Hall(String hallName, Integer hallCapacity) {
        this.hallName = hallName;
        this.hallCapacity = hallCapacity;
    }
}
