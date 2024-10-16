package com.movinfo.crawler;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBController {

    private static final String MONGO_URL = System.getenv("MONGO_URL");
    private MongoClient mongoClient;

    public MongoDBController(){
        mongoClient = MongoClients.create(MONGO_URL);
    }

    public MongoDatabase getMongoDatabase(String db){
        return mongoClient.getDatabase(db);
    }

    public void checkAndSetTTLIndex(MongoCollection<Document> collection){
        boolean indexExists = false;
        List<Document> indexes = collection.listIndexes().into(new ArrayList<>());
        for (Document index : indexes) {
            if (index.get("key").equals(new Document("expireAt", 1))) {
                indexExists = true;
                break;
            }
        }

        if (!indexExists) {
            IndexOptions indexOptions = new IndexOptions().expireAfter(0L, java.util.concurrent.TimeUnit.SECONDS);
            collection.createIndex(new Document("expireAt", 1), indexOptions);
        }
    }

    public void cleanUp(){
        if (mongoClient != null){
            mongoClient.close();
        }
    }
}
