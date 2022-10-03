package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@ToString
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

    @OneToMany(mappedBy = "movieSession")
    @ToString.Exclude
    private List<Ticket> movieSessionTickets;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
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
