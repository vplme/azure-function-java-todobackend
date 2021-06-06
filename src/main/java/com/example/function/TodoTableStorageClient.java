package com.example.function;

import com.example.function.entities.Todo;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Client for connecting to Azure Table Storage.
 */
public class TodoTableStorageClient {

  static final String AZURE_STORAGE_TODO_CONNECTION_STRING = "AzureStorageTodoConnectionString";
  static final String PARTITION_KEY = "PartitionKey";

  private final ExecutionContext context;
  private final CloudTable cloudTable;

  public TodoTableStorageClient(final ExecutionContext context)
      throws TodoTableException, URISyntaxException, StorageException {
    this.context = context;
    CloudStorageAccount storageAccount = getCloudStorageAccount();
    CloudTableClient tableClient = storageAccount.createCloudTableClient();

    // Create the table if it doesn't exist.
    cloudTable = tableClient.getTableReference(Todo.PARTITION_KEY);
    cloudTable.createIfNotExists();
  }

  private CloudStorageAccount getCloudStorageAccount() throws TodoTableException {
    try {
      var storageConnectionString = System.getenv(AZURE_STORAGE_TODO_CONNECTION_STRING);
      return CloudStorageAccount.parse(storageConnectionString);
    } catch (URISyntaxException e) {
      throw new TodoTableException("Could not parse ConnectionString", e);
    } catch (InvalidKeyException e) {
      throw new TodoTableException("Storage account key is invalid", e);
    }
  }

  public void insert(Todo todo) throws TodoTableException {
    TableOperation insert = TableOperation.insert(todo);
    try {
      cloudTable.execute(insert);
    } catch (StorageException e) {
      context.getLogger().severe("Could not insert Todo");
      context.getLogger().fine(() -> ExceptionUtils.getStackTrace(e));
      throw new TodoTableException("Could not insert todo", e);
    }
  }

  public void replace(Todo todo) throws TodoTableException {
    TableOperation insert = TableOperation.replace(todo);
    try {
      cloudTable.execute(insert);
    } catch (StorageException e) {
      context.getLogger().severe("Could not replace Todo");
      context.getLogger().fine(() -> ExceptionUtils.getStackTrace(e));
      throw new TodoTableException("Could not replace Todo", e);
    }
  }

  public void delete(Todo todo) throws TodoTableException {
    TableOperation deletion = TableOperation.delete(todo);
    try {
      cloudTable.execute(deletion);
    } catch (StorageException e) {
      throw new TodoTableException("Could not delete todo", e);
    }
  }

  public void deleteAll() throws StorageException {
    Iterable<Todo> todos = getAll();

    TableBatchOperation batchOperation = new TableBatchOperation();
    todos.forEach(batchOperation::delete);
    if (!batchOperation.isEmpty()) {
      cloudTable.execute(batchOperation);
    }
  }

  public Iterable<Todo> getAll() {
    String partitionQuery = TableQuery.generateFilterCondition(PARTITION_KEY, QueryComparisons.EQUAL, Todo.PARTITION_KEY);
    TableQuery<Todo> allTodosQuery = TableQuery.from(Todo.class).where(partitionQuery);

    return cloudTable.execute(allTodosQuery);
  }

  public Optional<Todo> getById(String id)   {
    TableOperation retrieval = TableOperation.retrieve(Todo.PARTITION_KEY, id, Todo.class);

    Todo todo = null;
    try {
      todo = cloudTable.execute(retrieval).getResultAsType();
    } catch (StorageException e) {
      e.printStackTrace();
    }

    return Optional.ofNullable(todo);
  }

  public class TodoTableException extends Exception {
    public TodoTableException(String errorMessage, Throwable err) {
      super(errorMessage, err);
    }
  }
}
