package com.movinfo.crawler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.movinfo.controller.MovieController;
import com.movinfo.repository.MovieRepository;
import com.movinfo.service.MovieService;
import com.movinfo.view.MovieView;

public class App 
{
    public static void main( String[] args )
    {
        MongoDBController mongoDBController = new MongoDBController();
        
        MovieRepository movieRepository = new MovieRepository(mongoDBController.getMongoDatabase("movinfo"));
        MovieService movieService = new MovieService(movieRepository);
        MovieView movieView = new MovieView();
        MovieController movieController = new MovieController(movieService, movieView);

        CGVCrawler crawler = new CGVCrawler();
        for (int i = 0; i < 20; ++i){
            LocalDate localDate = LocalDate.now().plusDays(i);
            List<String> openMovieList = crawler.checkImaxMovie(localDate);
            
            for (String movie : openMovieList){
                String date = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                System.out.println("Find Movie - " + movie + " : " + date);
                movieController.registerMovie(movie, date);
            }
        }
        
    }
}
