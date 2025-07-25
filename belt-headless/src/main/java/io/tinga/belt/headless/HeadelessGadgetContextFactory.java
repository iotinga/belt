package io.tinga.belt.headless;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import io.tinga.belt.Gadget;
import io.tinga.belt.GadgetContext;
import io.tinga.belt.GadgetContextFactoryImpl;
import io.tinga.belt.GadgetFatalException;
import io.tinga.belt.GadgetLifecycleException;
import io.tinga.belt.cli.CliCommandFactory;
import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.dummy.DummyGadgetCommandExecutor;
import io.tinga.belt.output.GadgetDisplayFactory;
import io.tinga.belt.output.Status;

public class HeadelessGadgetContextFactory extends GadgetContextFactoryImpl {

    private static final Logger log = LoggerFactory.getLogger(HeadelessGadgetContextFactory.class);

    public static final String COMMAND_LINE_PROPERTY = "CMD_LINE";
    public static final String COMMAND_LINE_GLUE = " ";

    private final CliCommandFactory commandFactory;

    @Inject
    public HeadelessGadgetContextFactory(Injector executorInjector, PropertiesProvider propertiesProvider,
            GadgetDisplayFactory displayFactory, CliCommandFactory commandFactory) {
        super(executorInjector, propertiesProvider, displayFactory);
        this.commandFactory = commandFactory;
    }

    @Override
    public Callable<Status> buildCallableFrom(String gadgetClassName) throws GadgetLifecycleException {
        Gadget<?> gadget = this.buildGadget(gadgetClassName);
        return this.buildCallableFrom(gadget);
    }

    @Override
    public <C extends Gadget.Command<?>> Callable<Status> buildCallableFrom(Gadget<C> gadget)
            throws GadgetLifecycleException {
        C command = this.buildCommand(gadget);
        GadgetContext<?> context = this.buildContextFrom(gadget, command);
        return this.buildCallableFrom(context);
    }

    private <C extends Gadget.Command<?>> C buildCommand(Gadget<C> gadget) throws GadgetLifecycleException {
        Properties properties = this.pp.properties(gadget.instanceName());
        C defaultCommand;

        try {
            defaultCommand = gadget.commandClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            log.error("A default constructor is required for command class {}", gadget.commandClass());
            throw new GadgetLifecycleException(new DummyGadgetCommandExecutor(), e);
        }

        String cmdLine = properties.getProperty(String.format(COMMAND_LINE_PROPERTY), null);

        try {
            if (cmdLine != null) {
                String[] args = cmdLine.trim().split(COMMAND_LINE_GLUE);
                return this.commandFactory.parseArgs(gadget, args);
            } else {
                return defaultCommand;
            }
        } catch (GadgetFatalException e) {
            log.error("Unable to convert %s to command: %s\n%s", COMMAND_LINE_PROPERTY, cmdLine, e.getMessage());
        }

        return null;
    }
}
