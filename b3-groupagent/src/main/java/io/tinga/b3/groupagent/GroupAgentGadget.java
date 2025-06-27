package io.tinga.b3.groupagent;

import com.google.inject.Module;

import io.tinga.b3.groupagent.cli.GroupAgentCliCommandExecutorModule;
import io.tinga.belt.AbstractGadget;

import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;
import it.netgrid.bauer.TopicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GroupAgentGadget extends AbstractGadget<GroupAgentCommandExecutor, GroupAgentCommand> {
    Logger log = LoggerFactory.getLogger(GroupAgentGadget.class);

    public static final String NAME = "GROUPAGENT";
    
    protected void configure() {
        bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Class<GroupAgentCommand> commandClass() {
        return GroupAgentCommand.class;
    }

    @Override
    public Class<GroupAgentCommandExecutor> executorClass() {
        return GroupAgentCommandExecutor.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(GroupAgentCommandOption.values());
    }

    @Override
    public com.google.inject.Module[] buildExecutorModules(Properties properties, GroupAgentCommand command) {
        log.debug("Building executor modules with properties {}", properties);
        if(command == null) {
            Module[] retval = {TopicFactory.getAsModule(properties), new GroupAgentCommandExecutorModule()};
            return retval;
        } else {
            Module[] retval = {TopicFactory.getAsModule(properties), new GroupAgentCliCommandExecutorModule(command)};
            return retval;
        }
    }
}
