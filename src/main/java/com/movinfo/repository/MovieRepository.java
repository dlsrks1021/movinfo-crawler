package com.movinfo.repository;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.movinfo.model.Movie;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

public class MovieRepository {
    private MongoCollection<Document> collection;

    public MovieRepository(MongoDatabase database){
        this.collection = database.getCollection("movies");
    }

    private boolean isMovieExists(String name, String date){
        return collection.find(and(eq("name", name), eq("date", date)))
                         .first() != null;
    }

    public void saveMovie(Movie movie){
        if (!isMovieExists(movie.getName(), movie.getDate())){
            Document doc = new Document("name", movie.getName())
                                .append("date", movie.getDate())
                                .append("screentype", movie.getScreentype())
                                .append("expireAt", movie.getExpireAt());
            collection.insertOne(doc);
            System.out.println(movie.getName() + "-" + movie.getDate() + "-" + movie.getScreentype() + "-(expireAt:" + movie.getExpireAt().toString() + ") - saved");
        } else{
            collection.updateOne(
                Filters.and(
                    Filters.eq("name", movie.getName()),
                    Filters.eq("date", movie.getDate())
                ),
                Updates.addToSet("screentype", movie.getScreentype())
            );
        }
    }
}
