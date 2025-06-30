package io.tinga.b3.entityagent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;
import io.tinga.b3.entityagent.operation.EntityOperationDaemon;

public class EntityCommandExecutorMQTT implements GadgetCommandExecutor<EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorMQTT.class);

    @Inject
    private EntityOperationDaemon daemon;

    @Override
    public CompletableFuture<Status> submit(EntityCommand command) {
        log.debug("command arrived");
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.completeAsync(new Supplier<Status>() {
            @Override
            public Status get() {
                daemon.run();
                return Status.OK;
            }
        });
        return retval;
    }

}
