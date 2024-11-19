package io.tinga.belt.headless;

import io.tinga.belt.output.AbstractGadgetDisplayFactory;
import io.tinga.belt.output.GadgetDisplay;
import io.tinga.belt.output.GadgetSlf4jDisplay;
import io.tinga.belt.GadgetContext;

public class HeadlessDisplayFactory extends AbstractGadgetDisplayFactory {
    
    public <C> GadgetDisplay buildDisplayInstance(GadgetContext<C> context) {
        return new GadgetSlf4jDisplay(context.output());
    }
    
}
