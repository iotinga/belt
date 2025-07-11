package io.tinga.belt.testgadget;

import java.util.concurrent.CompletableFuture;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

public class TestGadgetExecutor implements GadgetCommandExecutor {

    @Override
    public CompletableFuture<Status> submit(Object command) {
        return new CompletableFuture<>();
    }

}
