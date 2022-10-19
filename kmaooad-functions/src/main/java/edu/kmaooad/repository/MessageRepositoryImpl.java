package edu.kmaooad.repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kmaooad.config.MongoProperties;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

  private final MongoClientSettings settings;
  private final MongoProperties mongoProperties;

  public void insertMessage(String message) {
    try (MongoClient mongoClient = MongoClients.create(settings); ) {
      final MongoDatabase database = mongoClient.getDatabase(mongoProperties.getDatabase());
      final MongoCollection<Document> collection =
          database.getCollection(mongoProperties.getCollection());
      collection.insertOne(Document.parse(message));
    }
  }
}
