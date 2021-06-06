package com.example.function.triggers;

import com.example.function.TodoTableStorageClient;
import com.example.function.TodoTableStorageClient.TodoTableException;
import com.example.function.entities.Todo;
import com.example.function.helpers.GsonHelper;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger for creating a new To-do
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class PostTodo {

    private TodoTableStorageClient client;

    /**
     * POST /api/todos
     */
    @FunctionName("PostTodo")
    public HttpResponseMessage run(
            @HttpTrigger(route = "todos", name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)
        throws URISyntaxException, StorageException, TodoTableException {

        client = new TodoTableStorageClient(context);
        GsonHelper gson = new GsonHelper();
        Optional<String> result = request.getBody()
            .map(gson::convertToTodo)
            .flatMap(this::saveTodo)
            .map(gson::convertToString);

       return result.map(res -> request.createResponseBuilder(HttpStatus.OK).body(res).build())
           .orElse(request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Request failed").build());
    }

    private Optional<Todo> saveTodo(Todo todo) {
        try {
            client.insert(todo);
        } catch (TodoTableException e) {
            return Optional.empty();
        }
        return Optional.of(todo);
    }
}
