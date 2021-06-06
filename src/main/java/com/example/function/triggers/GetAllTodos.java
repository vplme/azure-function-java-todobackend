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
 * Azure Functions with HTTP Trigger to get all To-dos
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class GetAllTodos {
    /**
     * GET /api/todos
     */
    @FunctionName("GetAllTodos")
    public HttpResponseMessage run(
            @HttpTrigger(route = "todos", name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)
        throws URISyntaxException, StorageException, TodoTableException {

        TodoTableStorageClient client = new TodoTableStorageClient(context);
        Iterable<Todo> all = client.getAll();
        List<Todo> result = new ArrayList<>();
        all.forEach(result::add);

        GsonHelper gson = new GsonHelper();

        String json = gson.convertToString(result);
        return request.createResponseBuilder(HttpStatus.OK).body(json).build();
    }
}
