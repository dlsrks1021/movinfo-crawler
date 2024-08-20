package com.movinfo.service;

import com.movinfo.model.Movie;
import com.movinfo.repository.MovieRepository;

public class MovieService {
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public void registerMovie(String name, String date){
        Movie movie = new Movie(name, date);
        movieRepository.saveMovie(movie);
    }
}
