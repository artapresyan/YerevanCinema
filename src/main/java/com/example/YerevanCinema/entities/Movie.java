package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long movieID;

    @Column(name = "name", unique = true)
    @NotBlank
    private String movieName;

    @Column(name = "category")
    @NotBlank
    private String movieCategory;

    @Column(name = "description")
    private String movieDescription;

    @Column(name = "language")
    private String movieLanguage;

    @OneToMany(mappedBy = "movie")
    @ToString.Exclude
    private Set<MovieSession> movieSessions;

    public Movie(String movieName, String movieCategory, String movieDescription, String movieLanguage) {
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.movieLanguage = movieLanguage;
        this.movieCategory = movieCategory;
    }
}
