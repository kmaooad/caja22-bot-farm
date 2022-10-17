package edu.kmaooad.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoRepository {

    private static final String uri = "mongodb+srv://user:Password1@cluster0.jtpouhq.mongodb.net/?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "telegram";
    private static final String COLLECTION_NAME = "messages";

    private static final ConnectionString connectionString = new ConnectionString(uri);
    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .serverApi(ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build())
            .build();

    public void insertMessage(String message) {
        try (MongoClient mongoClient = MongoClients.create(settings);) {
            final MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            final MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            collection.insertOne(Document.parse(message));
        }
    }
}
