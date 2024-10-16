package com.movinfo.crawler;

import java.time.LocalDate;
import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.movinfo.controller.MovieController;
import com.movinfo.model.Movie;
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
        updateMissingScreentype(mongoDBController);
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

    /**
     * Old version of Movinfo-Crawler have no screentype but they are imax.
     * So, save screentype as imax if there is no screentype
     * 
     * @param mongoDBController
     * 
     */
    public void updateMissingScreentype(MongoDBController mongoDBController) {
        mongoDBController.getMongoDatabase("movinfo").getCollection("movies").updateMany(
            Filters.exists("screentype", false),
            Updates.addToSet("screentype", "imax")
        );
    }

    private void registerMovies(MovieController movieController, CGVCrawler cgvCrawler){
        LocalDate targetDateToStartCheck = LocalDate.now().plusDays(1);
        List<Movie> openMovieList = cgvCrawler.getOpenMovies(targetDateToStartCheck);
        movieController.registerMovies(openMovieList);
    }
}
