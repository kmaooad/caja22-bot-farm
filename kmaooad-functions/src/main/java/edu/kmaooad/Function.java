package edu.kmaooad;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private final Pattern pattern = Pattern.compile("(?:\\s*\"message_id\":\\s*)([0-9]+)");
    private static final String uri = "mongodb+srv://user:Password1@cluster0.jtpouhq.mongodb.net/?retryWrites=true&w=majority";

    /**
     * This function listens at endpoint "/api/TelegramWebhook". To invoke it using "curl" command in bash:
     * curl -d "HTTP Body" {your host}/api/TelegramWebhook
     */
    @FunctionName("TelegramWebhook")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        final String message = request.getBody().orElse(null);
        if (message == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a name in the request body")
                    .build();
        }

        final ConnectionString connectionString = new ConnectionString(uri);
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        try (MongoClient mongoClient = MongoClients.create(settings);) {
            final MongoDatabase database = mongoClient.getDatabase("telegram");
            final MongoCollection<Document> collection = database.getCollection("messages");
            collection.insertOne(Document.parse(message));
        }

        final Matcher matcher = pattern.matcher(message);
        if(!matcher.find()) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Can't get message_id from request")
                    .build();
        } else {
            final Integer messageId = Integer.parseInt(matcher.group(1));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(messageId).build();
        }
    }
}
