package com.movinfo.controller;

import java.util.Date;

import com.movinfo.service.MovieService;

public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService service){
        this.movieService = service;
    }

    public void registerMovie(String name, String date, Date expireAt){
        movieService.registerMovie(name, date, expireAt);
    }
}
