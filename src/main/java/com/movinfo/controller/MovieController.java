package com.movinfo.controller;

import java.util.Date;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.movinfo.service.MovieService;
import com.movinfo.model.Movie;

public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService service){
        this.movieService = service;
    }

    public void registerMovie(String name, String date, String screentype, Date expireAt){
        movieService.registerMovie(name, date, screentype, expireAt);
    }

    public void registerMovies(List<Movie> movies){
        movies.forEach((movie) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateTime expireDateTime = LocalDate.parse(movie.getDate(), formatter).plusDays(3).atStartOfDay();
            Date expireAt = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
            movie.setExpireAt(expireAt);

            movieService.registerMovie(movie);
        });

        
    }
}
