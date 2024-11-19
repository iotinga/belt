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

import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.helpers.DummyGadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;
import io.tinga.belt.output.GadgetDisplayFactory;
import io.tinga.belt.output.GadgetSink;

public class GadgetContextFactoryImpl implements GadgetContextFactory {

    private static final Logger log = LoggerFactory.getLogger(GadgetContextFactoryImpl.class);

    private final Injector injector;
    private final PropertiesProvider pp;
    private final GadgetDisplayFactory displayFactory;

    @Inject
    public GadgetContextFactoryImpl(Injector executorInjector, PropertiesProvider propertiesProvider,
            GadgetDisplayFactory displayFactory) {
        this.displayFactory = displayFactory;
        this.injector = executorInjector;
        this.pp = propertiesProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> GadgetContext<C> buildContextFrom(String gadgetClassName, C command) throws GadgetLifecycleException {
        try {
            Class<Gadget<?, C>> gadgetModuleClazz = (Class<Gadget<?, C>>) Class.forName(gadgetClassName);
            Gadget<?, C> gadget = (Gadget<?, C>) gadgetModuleClazz.getDeclaredConstructor().newInstance();
            return this.buildContextFrom(gadget, command);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            log.debug(String.format("Cannot instantiate plugin %s: %s", gadgetClassName, e));
            throw new GadgetLifecycleException(new DummyGadgetCommandExecutor(), e);
        }
    }

    @Override
    public <C> GadgetContext<C> buildContextFrom(Gadget<?, C> gadget, C command) throws GadgetLifecycleException {
        try {
            Properties properties = this.pp.properties(gadget.name());
            Injector gadgetInjector = injector.createChildInjector(gadget);
            Injector executorInjector = gadgetInjector
                    .createChildInjector(gadget.buildExecutorModules(properties, command));
            GadgetCommandExecutor<C> executor = executorInjector.getInstance(gadget.executorClass());
            GadgetSink output = executorInjector.getInstance(GadgetSink.class);
            return new GadgetContext<C>(properties, null, executor, output);
        } catch (IllegalArgumentException | SecurityException e) {
            log.debug(String.format("Cannot instantiate plugin %s: %s", gadget.getClass().getName(), e));
            throw new GadgetLifecycleException(new DummyGadgetCommandExecutor(), e);
        }
    }

    @Override
    public Callable<Status> buildCallableFrom(String gadgetClassName) throws GadgetLifecycleException {
        GadgetContext<?> context = this.buildContextFrom(gadgetClassName, null);
        return this.buildCallableFrom(context);
    }

    @Override
    public <C> Callable<Status> buildCallableFrom(Gadget<?, C> gadget) throws GadgetLifecycleException {
        GadgetContext<?> context = this.buildContextFrom(gadget, null);
        return this.buildCallableFrom(context);
    }

    private <C> Callable<Status> buildCallableFrom(GadgetContext<?> context) throws GadgetLifecycleException {
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