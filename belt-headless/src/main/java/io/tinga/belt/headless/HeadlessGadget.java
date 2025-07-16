package io.tinga.belt.headless;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.google.inject.Module;
import com.google.inject.Singleton;

import io.tinga.belt.AbstractGadget;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;

public class HeadlessGadget extends AbstractGadget<HeadlessCommand> {

    public static final String STANDARD_NAME = "HEADLESS";

    private String instanceName = STANDARD_NAME;

    @Override
    protected void configure() {
        bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class).in(Singleton.class);
    }

    @Override
    public String instanceName() {
        return this.instanceName;
    }

    @Override
    public Class<HeadlessCommand> commandClass() {
        return HeadlessCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(HeadlessCommandOption.values());
    }

    @Override
    public Module[] buildExecutorModules(Properties properties, HeadlessCommand command) {
        Module[] retval = { new HeadlessExecutorModule(command) };
        return retval;
    }

    @Override
    public void setInstanceName(String name) {
        this.instanceName = name == null ? STANDARD_NAME : name;
    }

}
