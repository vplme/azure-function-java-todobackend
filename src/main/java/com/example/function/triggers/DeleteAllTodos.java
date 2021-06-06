package com.example.function.triggers;

import com.example.function.TodoTableStorageClient;
import com.example.function.TodoTableStorageClient.TodoTableException;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger for deleting all To-dos
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class DeleteAllTodos {
    /**
     * DELETE "/api/todos".
     */
    @FunctionName("DeleteAllTodos")
    public HttpResponseMessage run(
            @HttpTrigger(route = "todos", name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)
        throws URISyntaxException, InvalidKeyException, StorageException, TodoTableException {

        final TodoTableStorageClient client = new TodoTableStorageClient(context);
        client.deleteAll();

        return request.createResponseBuilder(HttpStatus.OK).build();
    }
}
