package io.tinga.belt.dummy;

import java.util.concurrent.CompletableFuture;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

public class DummyGadgetCommandExecutor implements GadgetCommandExecutor {

    @Override
    public CompletableFuture<Status> submit(Object command) {
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.complete(Status.OK);
        return retval;
    }
    
}
