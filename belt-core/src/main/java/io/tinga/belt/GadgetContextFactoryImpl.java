package io.tinga.belt;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import io.tinga.belt.Gadget.Command;
import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.dummy.DummyGadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;
import io.tinga.belt.output.GadgetDisplayFactory;
import io.tinga.belt.output.GadgetSink;

public class GadgetContextFactoryImpl implements GadgetContextFactory {

    public static final String GADGET_NAME_GLUE = "@";

    private static final Logger log = LoggerFactory.getLogger(GadgetContextFactoryImpl.class);

    protected final Injector injector;
    protected final PropertiesProvider pp;
    protected final GadgetDisplayFactory displayFactory;

    @Inject
    public GadgetContextFactoryImpl(Injector executorInjector, PropertiesProvider propertiesProvider,
            GadgetDisplayFactory displayFactory) {
        this.displayFactory = displayFactory;
        this.injector = executorInjector;
        this.pp = propertiesProvider;
    }

    @Override
    public Callable<Status> buildCallableFrom(String gadgetClassName) throws GadgetLifecycleException {
        Gadget<?> gadget = this.buildGadget(gadgetClassName);
        GadgetContext<?> context = this.buildContextFrom(gadget, null);
        return this.buildCallableFrom(context);
    }

    @Override
    public <C extends Command<?>> Callable<Status> buildCallableFrom(Gadget<C> gadget) throws GadgetLifecycleException {
        GadgetContext<C> context = this.buildContextFrom(gadget, null);
        return this.buildCallableFrom(context);
    }

    @Override
    public <C extends Command<?>> GadgetContext<C> buildContextFrom(Gadget<C> gadget, C command)
            throws GadgetLifecycleException {
        try {
            Properties properties = this.pp.properties(gadget.instanceName());
            Injector gadgetInjector = injector.createChildInjector(gadget);
            Injector executorInjector = gadgetInjector
                    .createChildInjector(gadget.buildExecutorModules(properties, command));
            GadgetCommandExecutor executor = executorInjector
                    .getInstance(GadgetCommandExecutor.class);
            GadgetSink output = executorInjector.getInstance(GadgetSink.class);
            return new GadgetContext<C>(properties, null, executor, output);
        } catch (Exception e) {
            log.error(String.format("Cannot instantiate gadget %s: %s",
                    gadget.getClass().getSimpleName(), e));

            Throwable current = e;
            StringBuffer traceMessageBuffer = new StringBuffer();
            while (current != null && current.getStackTrace().length > 0) {
                for (StackTraceElement element : current.getStackTrace()) {
                    traceMessageBuffer.append(element.toString()).append("\n");
                }
                traceMessageBuffer.append("\n");
                current = current.getCause();
            }
            String traceMessage = traceMessageBuffer.toString();
            log.debug(traceMessage);
            throw new GadgetLifecycleException(new DummyGadgetCommandExecutor(), e);
        }

    }

    @Override
    public <C extends Command<?>> GadgetContext<C> buildContextFrom(String gadgetClassName, C command)
            throws GadgetLifecycleException {
        Gadget<C> gadget = this.buildGadget(gadgetClassName);
        return this.buildContextFrom(gadget, command);
    }

    @SuppressWarnings("unchecked")
    protected final <C extends Gadget.Command<?>> Gadget<C> buildGadget(String gadgetClassName)
            throws GadgetLifecycleException {
        try {
            String[] gadgetParts = gadgetClassName.split(GADGET_NAME_GLUE);
            String className = gadgetParts[gadgetParts.length-1];
            String instanceName = gadgetParts[0];
            Class<Gadget<C>> gadgetModuleClazz = (Class<Gadget<C>>) Class.forName(className);
            Gadget<C> gadget = gadgetModuleClazz.getDeclaredConstructor().newInstance();
            if(gadgetParts.length > 1) {
                gadget.setInstanceName(instanceName);
            }
            return gadget;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            log.debug(String.format("Cannot instantiate plugin %s: %s", gadgetClassName, e));
            throw new GadgetLifecycleException(new DummyGadgetCommandExecutor(), e);
        }
    }

    protected final Callable<Status> buildCallableFrom(GadgetContext<?> context) throws GadgetLifecycleException {
        return () -> {
            try {
                Future<?> displayRun = displayFactory.buildDisplay(context);
                CompletableFuture<Status> commandRun = context.executor().submit(null);
                Status result = commandRun.get();
                displayRun.cancel(true);
                return result;
            } catch (Exception reason) {
                throw new GadgetLifecycleException(context.executor(), reason);
            }
        };
    }

}
