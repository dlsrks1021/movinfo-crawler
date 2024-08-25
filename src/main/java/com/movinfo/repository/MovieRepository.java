package com.movinfo.repository;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
                                .append("date", movie.getDate());
            collection.insertOne(doc);
            System.out.println(movie.getName() + "-" + movie.getDate() + " - saved");
        } else{
            // throw new IllegalArgumentException();
        }
    }
}
