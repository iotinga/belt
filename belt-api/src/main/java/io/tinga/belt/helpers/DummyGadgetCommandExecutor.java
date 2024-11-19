package io.tinga.belt.helpers;

import java.util.concurrent.CompletableFuture;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

public class DummyGadgetCommandExecutor implements GadgetCommandExecutor<DummyGadgetCommand> {

    @Override
    public CompletableFuture<Status> submit(DummyGadgetCommand command) {
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.complete(Status.OK);
        return retval;
    }
    
}
