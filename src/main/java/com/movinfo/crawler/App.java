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

public class App 
{
    public static void main( String[] args )
    {
        App app = new App();
        app.run();
    }

    private void run(){
        // init
        CGVCrawler cgvCrawler = initializeCGVCrawler();
        MongoDBController mongoDBController = initializeMongoDB();
        MovieController movieController = initializeMovieController(mongoDBController);

        // execute
        checkAndSetTTLIndex(mongoDBController);
        registerMovies(movieController, cgvCrawler);

        // cleanup
        cgvCrawler.cleanUp();
        mongoDBController.cleanUp();
    }

    private MongoDBController initializeMongoDB(){
        return new MongoDBController();
    }

    private MovieController initializeMovieController(MongoDBController mongoDBController){
        MovieRepository movieRepository = new MovieRepository(mongoDBController.getMongoDatabase("movinfo"));
        MovieService movieService = new MovieService(movieRepository);
        return new MovieController(movieService);
    }

    private CGVCrawler initializeCGVCrawler(){
        return new CGVCrawler();
    }

    private void checkAndSetTTLIndex(MongoDBController mongoDBController){
        mongoDBController.checkAndSetTTLIndex(
            mongoDBController.getMongoDatabase("movinfo").getCollection("movies")
        );
    }

    private void registerMovies(MovieController movieController, CGVCrawler cgvCrawler){
        LocalDate targetDateToStartCheck = LocalDate.now().plusDays(1);

        Map<String, List<String>> openMovieMap = cgvCrawler.checkImaxMovie(targetDateToStartCheck);

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
