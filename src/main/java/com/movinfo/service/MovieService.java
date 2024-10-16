package com.movinfo.service;

import java.util.Date;

import com.movinfo.model.Movie;
import com.movinfo.repository.MovieRepository;

public class MovieService {
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public void registerMovie(String name, String date, String screentype, Date expireAt){
        Movie movie = new Movie(name, date, screentype, expireAt);
        movieRepository.saveMovie(movie);
    }

    public void registerMovie(Movie movie){
        movieRepository.saveMovie(movie);
    }
}
