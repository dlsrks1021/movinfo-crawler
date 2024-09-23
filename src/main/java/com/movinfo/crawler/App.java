package com.movinfo.crawler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        LocalDate targetDateToStartCheck = LocalDate.now().plusDays(1);
        Map<String, List<String>> openMovieMap = crawler.checkImaxMovie(targetDateToStartCheck);
        
        openMovieMap.forEach((date, movieList) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateTime expireDateTime = LocalDate.parse(date, formatter).plusDays(3).atStartOfDay();
            Date expireAt = Date.from(expireDateTime.atZone(ZoneId.systemDefault()).toInstant());
            
            for (String movie : movieList){
                movieController.registerMovie(movie, date, expireAt);
            }
        });
    }
}
