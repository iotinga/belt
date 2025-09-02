package io.tinga.belt.headless;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.GadgetLifecycleException;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.GadgetSink;
import io.tinga.belt.output.Status;

public class HeadlessCommandExecutor implements GadgetCommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(HeadlessCommandExecutor.class);
    private static final int GRACEFUL_SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ExecutorService executor;
    private final CompletionService<Status> completionService;
    private final GadgetSink out;

    private final List<Future<Status>> gadgetsResults;
    private final Injector rootInjector;

    @Inject
    public HeadlessCommandExecutor(Injector rootInjector, ExecutorService executor, GadgetSink output) {
        this.rootInjector = rootInjector;
        this.out = output;
        this.executor = executor;
        this.completionService = new ExecutorCompletionService<>(executor);
        this.gadgetsResults = new ArrayList<>();
    }

    @Override
    public CompletableFuture<Status> submit(Object rawCommand) {
        HeadlessCommand command = (HeadlessCommand) rawCommand;
        CompletableFuture<Status> retval = new CompletableFuture<>();

        retval.completeAsync(new Supplier<Status>() {
            @Override
            public Status get() {
                if (command.gadgets() == null || command.gadgets().size() < 1) {
                    out.put("gadgets list is empty");
                    return Status.BAD_REQUEST;
                }

                for (String className : command.gadgets()) {
                    if (executor.isShutdown()) {
                        break;
                    }
                    try {
                        GadgetContextFactory gadgetContextFactory = rootInjector
                                .createChildInjector()
                                .getInstance(GadgetContextFactory.class);
                        Callable<Status> plugin = gadgetContextFactory.buildCallableFrom(className);
                        gadgetsResults.add(completionService.submit(plugin));
                    } catch (GadgetLifecycleException e) {
                        if (command.ignore()) {
                            out.put(String.format("Unable to submit %s: %s", className, e.getMessage()));
                            log.warn(String.format("Unable to submit %s: %s", className, e.getMessage()));
                        } else {
                            out.put(String.format("Unable to submit %s: %s", className, e.getMessage()));
                            log.error(String.format("Unable to submit %s: %s", className, e.getMessage()));
                            executor.shutdownNow();
                            break;
                        }
                    }
                }

                // wait for execution to end
                try {
                    while (!executor.isShutdown() && gadgetsResults.size() > 0) {
                        Future<Status> result = completionService.take();
                        gadgetsResults.remove(result);
                        try {
                            Status status = result.resultNow();
                            if (command.action() == HeadlessAction.SEQUENTIAL) {
                                log.info(String.format("Completed %s", status));
                            } else if (command.ignore()) {
                                log.warn(String.format("Completed %s", status));
                            } else {
                                log.error(String.format("Completed %s", status));
                                break;
                            }
                        } catch (IllegalStateException e) {
                            GadgetLifecycleException pluglinException = (GadgetLifecycleException) result
                                    .exceptionNow();
                            if (command.ignore()) {
                                log.warn(String.format("Abort with exception %s: %s",
                                        pluglinException.gadget.getClass().getSimpleName(),
                                        pluglinException.reason.getMessage()));
                            } else {
                                log.error(String.format("Abort with exception %s: %s",
                                        pluglinException.gadget.getClass().getSimpleName(),
                                        pluglinException.reason.getMessage()));
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.info(String.format("Interrupt %s", e.getMessage()));
                }

                // graceful shutdown if needed
                if (!executor.isShutdown()) {
                    executor.shutdown();
                    try {
                        executor.awaitTermination(GRACEFUL_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        if (executor.isTerminated()) {
                            log.info("Graceful shutdown success");
                        } else {
                            log.warn(String.format("Graceful shutdown was taking more than %d seconds",
                                    GRACEFUL_SHUTDOWN_TIMEOUT_SECONDS));
                        }
                    } catch (InterruptedException e) {
                        log.warn(String.format("Graceful shutdown interrupted: %s", e.getMessage()));
                    }
                }

                return Status.OK;
            }
        });
        return retval;
    }

}
