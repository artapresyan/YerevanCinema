package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter @Setter
@NoArgsConstructor
public class MovieSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long movieSessionID;

    @Column(name = "start")
    @NotNull
    private LocalDateTime movieSessionStart;

    @Column(name = "end")
    @NotNull
    private LocalDateTime movieSessionEnd;

    @Column(name = "price")
    @NotNull
    private Integer movieSessionPrice;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public MovieSession(LocalDateTime movieSessionStart, LocalDateTime movieSessionEnd, Integer movieSessionPrice,
                        Hall hall, Movie movie, Admin admin) {
        this.movieSessionStart = movieSessionStart;
        this.movieSessionEnd = movieSessionEnd;
        this.movieSessionPrice = movieSessionPrice;
        this.hall = hall;
        this.movie = movie;
        this.admin = admin;
    }
}
