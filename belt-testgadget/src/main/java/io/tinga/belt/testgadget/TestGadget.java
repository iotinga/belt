package io.tinga.belt.testgadget;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.inject.Module;

import io.tinga.belt.AbstractGadget;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetSink;

public class TestGadget extends AbstractGadget<TestGadgetCommand> {
    @Override
    protected void configure() {
        bind(GadgetCommandExecutor.class).to(TestGadgetExecutor.class);
        bind(GadgetSink.class).to(TestGadgetSink.class);
    }

    @Override
    public String name() {
        return "TEST";
    }

    @Override
    public Class<TestGadgetCommand> commandClass() {
        return TestGadgetCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(TestGadgetCommandOption.values());
    }

    @Override
    public Module[] buildExecutorModules(Properties properties, TestGadgetCommand command) {
        Module[] retval = {};
        return retval;
    }
}
