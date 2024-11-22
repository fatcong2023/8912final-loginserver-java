package loginserver;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.logging.Logger;
import java.util.List;
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

    // @FunctionName("AddStudentFunction")
    // public HttpResponseMessage addStudent(
    //     @HttpTrigger(name = "req", 
    //                  methods = {HttpMethod.POST},
    //                  authLevel = AuthorizationLevel.ANONYMOUS,
    //                  route = "add") HttpRequestMessage<Optional<String>> request,
    //     final ExecutionContext context) {

    //     Logger logger = context.getLogger();
    //     logger.info("HTTP POST trigger function processed a request.");

    //     String requestBody = request.getBody().orElse("");
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     String firstName, lastName, email;

    //     try {
    //         JsonNode jsonNode = objectMapper.readTree(requestBody);
    //         firstName = jsonNode.get("firstName").asText();
    //         lastName = jsonNode.get("lastName").asText();
    //         email = jsonNode.get("email").asText();

    //         if (firstName == null || firstName.isEmpty() ||
    //             lastName == null || lastName.isEmpty() ||
    //             email == null || email.isEmpty()) {
    //             return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
    //                           .body("Invalid content: firstname, lastname, and email are required.")
    //                           .build();
    //         }
    //     } catch (Exception e) {
    //         logger.severe("Invalid JSON format: " + e.getMessage());
    //         return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
    //                       .body("Invalid JSON format: " + e.getMessage())
    //                       .build();
    //     }

    //     try {
    //         DatabaseService.addStudent(firstName, lastName, email);
    //     } catch (Exception e) {
    //         logger.severe("Database insertion error: " + e.getMessage());
    //         return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
    //                       .body("Database insertion error: " + e.getMessage())
    //                       .build();
    //     }

    //     return request.createResponseBuilder(HttpStatus.OK)
    //                   .body("Student added successfully.")
    //                   .build();
    // }
}
