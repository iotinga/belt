package io.tinga.belt;

import java.util.List;
import java.util.Properties;

import com.google.inject.Module;

import io.tinga.belt.input.GadgetCommandOption;

public interface Gadget<C extends Gadget.Command<?>> extends Module {

    public interface Command<A> {
        A action();
    }

    public String name();

    public Class<C> commandClass();

    public List<GadgetCommandOption> commandOptions();

    public Module[] buildExecutorModules(Properties properties, C command);
}
