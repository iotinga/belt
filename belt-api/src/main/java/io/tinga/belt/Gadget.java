package io.tinga.belt;

import java.util.List;
import java.util.Properties;

import com.google.inject.Module;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandOption;

public interface Gadget<E extends GadgetCommandExecutor<C>, C> extends Module {
    public String name();

    public Class<C> commandClass();

    public Class<E> executorClass();

    public List<GadgetCommandOption> commandOptions();

    public Module[] buildExecutorModules(Properties properties, C command);
}
