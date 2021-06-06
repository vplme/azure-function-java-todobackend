package com.example.function.triggers;

import com.example.function.TodoTableStorageClient;
import com.example.function.TodoTableStorageClient.TodoTableException;
import com.example.function.helpers.GsonHelper;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger for getting a specific To-do
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class GetTodoById {
    /**
     * GET /api/todos/{id}
     */
    @FunctionName("GetTodoById")
    public HttpResponseMessage run(
            @HttpTrigger(route = "todos/{id}", name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context, @BindingName("id") String id)
        throws URISyntaxException, InvalidKeyException, StorageException, TodoTableException {
        context.getLogger().info("Java HTTP trigger processed a request.");
        GsonHelper gson = new GsonHelper();

        TodoTableStorageClient client = new TodoTableStorageClient(context);
        return client.getById(id)
            .map(gson::convertToString)
            .map(json -> request.createResponseBuilder(HttpStatus.OK).body(json).build())
            .orElse(request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Request failed").build());
    }
}
