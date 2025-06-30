package io.tinga.b3.entityagent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.GadgetSink;
import io.tinga.belt.output.Status;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageProvider;
import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.entityagent.operation.EntityOperation;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.InvalidEntityOperationException;

public class EntityCommandExecutorDefault implements GadgetCommandExecutor<EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorDefault.class);

    @Inject
    private EntityOperationGrantsChecker checker;

    @Inject
    private DesiredEntityMessageProvider provider;

    @Inject
    private EntityOperationFactory operationFactory;

    @Inject
    private GadgetSink out;

    @Override
    public CompletableFuture<Status> submit(EntityCommand command) {
        log.debug("command arrived");
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.completeAsync(new Supplier<Status>() {
            @Override
            public Status get() {
                try {
                    EntityMessage message = provider.load(command.desiredRef());
                    EntityOperation operation = operationFactory.buildFrom(command.topic(), message);
                    boolean result = checker.isAllowed(operation);
                    if (result) {
                        out.put(String.format("[ALLOWED]"));
                    } else {
                        out.put(String.format("[DENIED]"));
                    }
                } catch (InvalidEntityOperationException e) {
                    out.put(String.format("[INVALID]"));
                }
                return Status.OK;
            }
        });
        return retval;
    }

}
