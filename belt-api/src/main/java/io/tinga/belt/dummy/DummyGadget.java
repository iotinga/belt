package io.tinga.belt.dummy;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.belt.AbstractGadget;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetSink;

public class DummyGadget extends AbstractGadget<DummyGadgetCommandExecutor, DummyGadgetCommand> {

    public static final String NAME = "DUMMY";

    @Override
    protected void configure() {
        bind(Key.get(new TypeLiteral<GadgetSink>() {})).to(DummyGadgetSink.class).in(Singleton.class);
    }

    @Override
    public String name() {
        return DummyGadget.NAME;
    }

    @Override
    public Class<DummyGadgetCommand> commandClass() {
        return DummyGadgetCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(DummyGadgetCommandOptions.values());
    }

    @Override
    public Class<DummyGadgetCommandExecutor> executorClass() {
        return DummyGadgetCommandExecutor.class;
    }

    @Override
    public Module[] buildExecutorModules(Properties properties, DummyGadgetCommand command) {
        Module[] retval = {};
        return retval;
    }

}
