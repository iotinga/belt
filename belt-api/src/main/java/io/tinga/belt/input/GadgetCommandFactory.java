package io.tinga.belt.input;

import io.tinga.belt.Gadget;
import io.tinga.belt.GadgetFatalException;

public interface GadgetCommandFactory {
    public <C> C parseArgs(Gadget<?, C> gadget, String[] args) throws GadgetFatalException;
}
