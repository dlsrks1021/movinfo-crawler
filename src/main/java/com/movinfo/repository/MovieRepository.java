package com.movinfo.repository;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.movinfo.model.Movie;
import com.movinfo.model.Screen;

import static com.mongodb.client.model.Filters.eq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MovieRepository {
    private MongoCollection<Document> collection;

    public MovieRepository(MongoDatabase database){
        this.collection = database.getCollection("movies");
    }

    public boolean isMovieExists(String name){
        return collection.find(eq("name", name))
                         .first() != null;
    }

    private byte[] downloadImage(String imageSrc){
        try {
            URL url = new URL(imageSrc);
            InputStream in = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1){
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e){
            return null;
        }
    }

    public void saveMovie(Movie movie){
        Document doc = new Document("name", movie.getName())
                            .append("dateOpen", movie.getDateOpen())
                            .append("poster", downloadImage(movie.getPoster()))
                            .append("expireAt", movie.getExpireAt());
        collection.insertOne(doc);
    }

    public void updateScreen(Screen screen){
        collection.updateOne(
            Filters.and(
                Filters.eq("name", screen.getMovieName())
            ),
            Updates.set("screen." + screen.getScreenDate(), screen.getScreentypes())
        );
    }
}
