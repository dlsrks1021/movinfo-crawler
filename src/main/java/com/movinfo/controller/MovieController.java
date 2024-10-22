package com.movinfo.controller;

import com.movinfo.service.MovieService;
import com.movinfo.model.Movie;
import com.movinfo.model.Screen;

public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService service){
        this.movieService = service;
    }

    public void registerMovie(Movie movie){
        movieService.registerMovie(movie);
    }

    public void updateMovieByScreen(Screen screen){
        movieService.updateMovieByScreen(screen);
    }
}
