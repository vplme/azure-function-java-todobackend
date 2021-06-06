package com.example.function.triggers;

import com.example.function.TodoTableStorageClient;
import com.example.function.TodoTableStorageClient.TodoTableException;
import com.example.function.entities.Todo;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger for deleting a specific To-do
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class DeleteTodoById {
    /**
     * DELETE "/api/todos/{id}".
     */
    @FunctionName("DeleteTodoById")
    public HttpResponseMessage run(
            @HttpTrigger(route = "todos/{id}", name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context, @BindingName("id") String id)
        throws URISyntaxException, StorageException, TodoTableException {

        TodoTableStorageClient client = new TodoTableStorageClient(context);
        Optional<Todo> fetchedTodo = client.getById(id);
        if (fetchedTodo.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No such todo").build();
        }

        try {
            client.delete(fetchedTodo.orElse(null));
        } catch (TodoTableException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
        }
        return request.createResponseBuilder(HttpStatus.OK).build();

    }
}
