package io.tinga.belt.cli;

import io.tinga.belt.GadgetContext;
import io.tinga.belt.output.AbstractGadgetDisplayFactory;
import io.tinga.belt.output.GadgetDisplay;
import io.tinga.belt.output.GadgetSystemDisplay;

public class CliDisplayFactory extends AbstractGadgetDisplayFactory {
    
    public <C> GadgetDisplay buildDisplayInstance(GadgetContext<C> context) {
        return new GadgetSystemDisplay(context.output());
    }
    
}
