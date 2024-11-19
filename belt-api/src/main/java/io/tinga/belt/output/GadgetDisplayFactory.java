package io.tinga.belt.output;

import java.util.concurrent.Future;

import io.tinga.belt.GadgetContext;

public interface GadgetDisplayFactory {
    public <C> Future<?> buildDisplay(GadgetContext<C> context);
}
