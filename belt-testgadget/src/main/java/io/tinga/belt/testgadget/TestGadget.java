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


    public static final String STANDARD_NAME = "TEST";

    private String instanceName = STANDARD_NAME;

    @Override
    protected void configure() {
        bind(GadgetCommandExecutor.class).to(TestGadgetExecutor.class);
        bind(GadgetSink.class).to(TestGadgetSink.class);
    }

    @Override
    public String instanceName() {
        return this.instanceName;
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

    @Override
    public void setInstanceName(String name) {
        this.instanceName = name == null ? STANDARD_NAME : name;
    }
}
