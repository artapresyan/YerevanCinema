package com.example.YerevanCinema.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "movies")
@Getter @Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long movieID;

    @Column(name = "name", unique = true)
    @NotBlank
    private String movieName;

    @Column(name = "description", columnDefinition ="default 'no description'" )
    private String movieDescription;

    @Column(name = "language", columnDefinition = "default 'not specified'")
    private String movieLanguage;

    public Movie(String movieName, String movieDescription, String movieLanguage) {
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.movieLanguage = movieLanguage;
    }
}
