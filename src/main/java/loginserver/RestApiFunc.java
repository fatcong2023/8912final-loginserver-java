package loginserver;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.logging.Logger;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiFunc {
    @FunctionName("RestApiFunction")
    public HttpResponseMessage getHello(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.GET},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "get") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {


        Logger logger = context.getLogger();
        logger.info("HTTP trigger function processed a request.");


        return request.createResponseBuilder(HttpStatus.OK)
                      .header("Content-Type", "application/json")
                      .body("Hello, Azure Functions!")
                      .build();
    }

    @FunctionName("OptionsHandler")
    public HttpResponseMessage options(
    @HttpTrigger(name = "req",
                 methods = {HttpMethod.OPTIONS},
                 authLevel = AuthorizationLevel.ANONYMOUS,
                 route = "{*any}") HttpRequestMessage<Optional<String>> request,
    final ExecutionContext context) {

    return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                  .header("Access-Control-Allow-Origin", "*")
                  .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                  .header("Access-Control-Allow-Headers", "Content-Type")
                  .build();
}
    
    @FunctionName("RegisterUserFunction")
    public HttpResponseMessage registerUser(
        @HttpTrigger(name = "req",
                     methods = {HttpMethod.POST},
                     authLevel = AuthorizationLevel.ANONYMOUS,
                     route = "register") HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info("HTTP POST trigger function processed a request.");

        String requestBody = request.getBody().orElse("");
        ObjectMapper objectMapper = new ObjectMapper();
        String username, email, lastName, firstName, phoneNumber;

        try {
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            username = jsonNode.get("username").asText();
            email = jsonNode.get("email").asText();
            lastName = jsonNode.get("lastName").asText();
            firstName = jsonNode.get("firstName").asText();
            phoneNumber = jsonNode.get("phoneNumber").asText();

            if (username == null || username.isEmpty() ||
                email == null || email.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                firstName == null || firstName.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "*")
                              .body("Invalid content: username, email, lastName, and firstName are required.")
                              .build();
            }
        } catch (Exception e) {
            logger.severe("Invalid JSON format: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
            .header("Access-Control-Allow-Origin", "*")
                          .body("Invalid JSON format: " + e.getMessage())
                          .build();
        }

        try {
            DatabaseOperations.insertUser(firstName, lastName, username, phoneNumber, email, logger);
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Access-Control-Allow-Origin", "*")
                          .body("Database insertion error: " + e.getMessage())
                          .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
        .header("Access-Control-Allow-Origin", "*")
                      .body("User registered successfully.")
                      .build();
    }
}
