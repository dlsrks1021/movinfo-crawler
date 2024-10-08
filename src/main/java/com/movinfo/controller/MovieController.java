package com.movinfo.controller;

import java.util.Date;

import com.movinfo.service.MovieService;
import com.movinfo.view.MovieView;

public class MovieController {
    private MovieService movieService;
    private MovieView movieView;

    public MovieController(MovieService service, MovieView view){
        this.movieService = service;
        this.movieView = view;
    }

    public void registerMovie(String name, String date, Date expireAt){
        movieService.registerMovie(name, date, expireAt);
    }
}
