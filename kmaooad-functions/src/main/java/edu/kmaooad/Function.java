package edu.kmaooad;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.exception.InvalidMessageException;
import edu.kmaooad.parser.RequestParser;
import edu.kmaooad.repository.MongoRepository;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private MongoRepository mongoRepository = new MongoRepository();
    private RequestParser requestParser = new RequestParser();

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
                    .body("Request body can't be empty")
                    .build();
        }

        mongoRepository.insertMessage(message);

        try {
            final Integer messageId = requestParser.getMessageId(message);
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(messageId).build();
        } catch (InvalidMessageException ex) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage())
                    .build();
        }
    }

    public void setMongoRepository(MongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    public void setRequestParser(RequestParser requestParser) {
        this.requestParser = requestParser;
    }
}
