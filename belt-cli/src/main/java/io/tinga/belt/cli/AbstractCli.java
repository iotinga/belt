package io.tinga.belt.cli;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import io.tinga.belt.GadgetFatalException;
import io.tinga.belt.GadgetLifecycleException;
import io.tinga.belt.Gadget;
import io.tinga.belt.GadgetContext;
import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.output.GadgetSystemDisplay;
import io.tinga.belt.output.Status;

public abstract class AbstractCli<C extends Gadget.Command<?>> implements Runnable {

    public static final Module[] DEFAULT_ROOT_MODULES = { new CliRootModule() };
    private static final Logger log = LoggerFactory.getLogger(AbstractCli.class);

    private final Module[] rootModules;
    private final String[] args;
    protected final Gadget<C> gadget;

    private ExecutorService displayExecutor;
    private Injector cliInjector;
    private GadgetCommandFactory commandFactory;
    private GadgetContextFactory contextFactory;

    public AbstractCli(String[] args, Gadget<C> gadget, Module... customRootModules) {
        this.args = args;
        this.gadget = gadget;
        this.displayExecutor = Executors.newSingleThreadExecutor();

        Module[] basicRootModules = this.buildRootModules();

        if (customRootModules != null && customRootModules.length > 0) {
            this.rootModules = new Module[basicRootModules.length + customRootModules.length];
            System.arraycopy(basicRootModules, 0, this.rootModules, 0, basicRootModules.length);
            System.arraycopy(customRootModules, 0, this.rootModules, basicRootModules.length, customRootModules.length);
        } else {
            this.rootModules = new Module[basicRootModules.length];
            System.arraycopy(basicRootModules, 0, this.rootModules, 0, basicRootModules.length);
        }

    }

    public void init() {
        cliInjector = Guice.createInjector(rootModules);
        commandFactory = cliInjector.getInstance(Key.get(GadgetCommandFactory.class));
        contextFactory = cliInjector.getInstance(GadgetContextFactory.class);
    }

    public void run() {
        Status status = Status.INTERNAL_SERVER_ERROR;
        GadgetContext<C> context = null;
        int exitCode = 1;
        Throwable cause = null;

        try {
            log.info("Initializing CLI for gadget: {}", gadget.instanceName());
            log.debug("CLI arguments: {}", (Object) args);

            this.init();
            log.info("Injector and factories initialized.");

            C command = this.parseCommand(args);
            log.info("Parsed command: {}", command.getClass().getSimpleName());

            context = this.contextFactory.buildContextFrom(gadget, command);
            log.info("Context created.");

            this.displayExecutor.submit(new GadgetSystemDisplay(context.output()));
            log.debug("Display executor submitted.");

            CompletableFuture<Status> commandRun = context.executor().submit(command);
            log.info("Command execution started...");
            status = commandRun.get();
            log.info("Command completed with status: {}", status);
        } catch (GadgetFatalException e) {
            log.error("Fatal error: {}", e.getMessage(), e);
            exitCode = e.exitCode;
            cause = e;
        } catch (GadgetLifecycleException e) {
            log.error("Lifecycle error: {}", e.reason.getMessage(), e);
            exitCode = 1;
            cause = e;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Execution error. Exit({}): {}", status.getCode(), e.getMessage(), e);
            exitCode = 1;
            cause = e;
        } catch (Exception e) {
            log.error("Unexpected error. Exit({}): {}", status.getCode(), e.getMessage(), e);
            exitCode = 1;
            cause = e;
        } finally {
            try {
                if (context != null) {
                    log.debug("Closing output stream...");
                    context.output().close();
                }
            } catch (Exception e) {
                log.warn("Failed to close output stream: {}", e.getMessage(), e);
            }

            try {
                log.debug("Shutting down display executor...");
                this.displayExecutor.shutdown();
            } catch (Exception e) {
                log.warn("Failed to shut down executor: {}", e.getMessage(), e);
            }

            if (status.getCategory() == Status.Category.SUCCESS && cause == null) {
                exitCode = 0;
            }

            log.info("Exiting CLI with code {}", exitCode);
            System.exit(exitCode);
        }
    }

    public C parseCommand(String[] args) throws GadgetFatalException {
        return commandFactory.parseArgs(gadget, args);
    }

    public String name() {
        return String.format("%s-cli", this.gadget.instanceName().toLowerCase());
    }

    public abstract Module[] buildRootModules();

}
