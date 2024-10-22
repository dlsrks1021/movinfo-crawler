package com.movinfo.crawler;

import java.time.LocalDate;
import java.util.List;

import com.movinfo.controller.MovieController;
import com.movinfo.model.Movie;
import com.movinfo.model.Screen;
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
        checkMoviesOpen(movieController, cgvCrawler);
        checkScreensOpen(movieController, cgvCrawler);

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

    private void checkScreensOpen(MovieController movieController, CGVCrawler cgvCrawler){
        LocalDate targetDateToStartCheck = LocalDate.now().plusDays(1);
        List<Screen> screenList = cgvCrawler.getOpenScreens(targetDateToStartCheck);

        screenList.forEach((screen) -> {
            movieController.updateMovieByScreen(screen);
        });
    }

    private void checkMoviesOpen(MovieController movieController, CGVCrawler cgvCrawler){
        List<Movie> movieList = cgvCrawler.getOpenMovies();

        movieList.forEach((movie) -> {
            movieController.registerMovie(movie);
        });
    }
}
