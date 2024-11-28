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

        logger.info(requestBody);
        ObjectMapper objectMapper = new ObjectMapper();
        String username, email, lastName, firstName, phoneNumber, password;

        try {
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            username = jsonNode.get("username").asText();
            email = jsonNode.get("email").asText();
            lastName = jsonNode.get("lastName").asText();
            firstName = jsonNode.get("firstName").asText();
            phoneNumber = jsonNode.get("phoneNumber").asText();
            password = jsonNode.get("password").asText();

            if (username == null || username.isEmpty() ||
                email == null || email.isEmpty() ||
                lastName == null || lastName.isEmpty() || password.isEmpty() || password == null ||
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
            DatabaseOperations.insertUser(firstName, lastName, username, phoneNumber, email, password, logger);
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

    @FunctionName("login")
public HttpResponseMessage login(
    @HttpTrigger(name = "req",
                 methods = {HttpMethod.POST},
                 authLevel = AuthorizationLevel.ANONYMOUS,
                 route = "login") HttpRequestMessage<Optional<String>> request,
    final ExecutionContext context) {
    Logger logger = context.getLogger();
    String username;
    String password;

    try {
        JsonNode jsonNode = new ObjectMapper().readTree(request.getBody().orElseThrow(() -> new IllegalArgumentException("Invalid JSON format")));
        username = jsonNode.get("username").asText();
        password = jsonNode.get("password").asText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "*")
                .body("Invalid content: username and password are required.")
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
        boolean isValidUser = DatabaseOperations.validateUser(username, password, logger);
        if (isValidUser) {
            return request.createResponseBuilder(HttpStatus.CREATED)
                .header("Access-Control-Allow-Origin", "*")
                .body("Login successful.")
                .build();
        } else {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                .header("Access-Control-Allow-Origin", "*")
                .body("Invalid username or password.")
                .build();
        }
    } catch (Exception e) {
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Access-Control-Allow-Origin", "*")
            .body("Database error: " + e.getMessage())
            .build();
    }
}
}
