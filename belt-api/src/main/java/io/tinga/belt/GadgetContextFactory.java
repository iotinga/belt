package io.tinga.belt;

import java.util.concurrent.Callable;

import io.tinga.belt.output.Status;


public interface GadgetContextFactory {
    public Callable<Status> buildCallableFrom(String gadgetClassName) throws GadgetLifecycleException;
    public <C> Callable<Status> buildCallableFrom(Gadget<?, C> gadget) throws GadgetLifecycleException;
    public <C> GadgetContext<C> buildContextFrom(Gadget<?, C> gadget, C command) throws GadgetLifecycleException;
    public <C> GadgetContext<C> buildContextFrom(String gadgetClassName, C command) throws GadgetLifecycleException;
}
