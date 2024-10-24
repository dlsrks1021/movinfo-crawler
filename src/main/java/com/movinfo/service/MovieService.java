package com.movinfo.service;

import java.util.Calendar;
import java.util.Date;

import com.movinfo.model.Movie;
import com.movinfo.model.Screen;
import com.movinfo.repository.MovieRepository;

public class MovieService {
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public void registerMovie(Movie movie){
        movie.setExpireAt(getExpireDate(movie.getDateOpen()));
        if (!movieRepository.isMovieExists(movie.getName())){
            movieRepository.saveMovie(movie);
            System.out.println(movie.getName() + " " + movie.getDateOpen() + " (expireAt: " + movie.getExpireAt().toString() + ") - saved");
        }
    }

    private void logScreen(Screen screen){
        StringBuilder logOutput = new StringBuilder();
        
        logOutput.append(screen.getMovieName() + " " + screen.getScreenDate());
        logOutput.append(" [");
        screen.getScreentypes().forEach((screentype) -> {
            logOutput.append(" " + screentype);
        });
        logOutput.append(" ]");

        System.out.println(logOutput.toString());
    }

    public void updateMovieByScreen(Screen screen){
        if (movieRepository.isMovieExists(screen.getMovieName())){
            movieRepository.updateScreen(screen);
            logScreen(screen);
        }
    }

    private Date getExpireDate(Date dateOpen){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOpen);
        calendar.add(Calendar.DAY_OF_YEAR, 90);

        return calendar.getTime();
    }
}
