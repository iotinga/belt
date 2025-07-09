package io.tinga.belt.input;

import java.util.concurrent.CompletableFuture;

import io.tinga.belt.output.Status;

public interface GadgetCommandExecutor {
    public CompletableFuture<Status> submit(Object command);
}
