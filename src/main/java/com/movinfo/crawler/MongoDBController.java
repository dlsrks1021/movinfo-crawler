package com.movinfo.crawler;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBController {

    private static final String MONGO_URL = System.getenv("MONGO_URL");
    private MongoClient mongoClient;

    public MongoDBController(){
        mongoClient = MongoClients.create(MONGO_URL);
    }

    public MongoDatabase getMongoDatabase(String db){
        return mongoClient.getDatabase(db);
    }
}
