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
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.output.GadgetSystemDisplay;
import io.tinga.belt.output.Status;

public abstract class AbstractCli<E extends GadgetCommandExecutor<C>, C> implements Runnable {

    public static final Module[] DEFAULT_ROOT_MODULES = { new CliRootModule() };
    private static final Logger log = LoggerFactory.getLogger(AbstractCli.class);

    private final Module[] rootModules;
    private final String[] args;
    protected final Gadget<E, C> gadget;

    private ExecutorService displayExecutor;
    private Injector cliInjector;
    private GadgetCommandFactory commandFactory;
    private GadgetContextFactory contextFactory;

    public AbstractCli(String[] args, Gadget<E, C> gadget, Module... customRootModules) {
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
        contextFactory = cliInjector.getInstance(Key.get(GadgetContextFactory.class));
    }

    public void run() {
        Status status = Status.INTERNAL_SERVER_ERROR;
        GadgetContext<C> context = null;
        try {
            this.init();
            C command = this.parseCommand(args);
            context = this.contextFactory.buildContextFrom(gadget, command);
            this.displayExecutor.submit(new GadgetSystemDisplay(context.output()));
            CompletableFuture<Status> commandRun = context.executor().submit(command);
            status = commandRun.get();
        } catch (GadgetFatalException e) {
            log.debug("Exit({})", e.exitCode);
            System.out.println(e.getMessage());
            System.exit(e.exitCode);
        } catch (GadgetLifecycleException e) {
            log.error("Exit({})", e.reason.getMessage());
            System.exit(1);
        } catch (InterruptedException | ExecutionException e) {
            log.info("Exit({}): {}", status.getCode(), e.getMessage());
        } finally {
            if (context != null) {
                context.output().close();
            }
            this.displayExecutor.shutdown();

            int exitCode = status.getCategory() == Status.Category.SUCCESS ? 0 : 1;

            log.info("Exiting with status {}", exitCode);
            System.exit(exitCode);
        }
    }

    public C parseCommand(String[] args) throws GadgetFatalException {
        return commandFactory.parseArgs(gadget, args);
    }

    public String name() {
        return String.format("%s-cli", this.gadget.name().toLowerCase());
    }

    public abstract Module[] buildRootModules();

}
