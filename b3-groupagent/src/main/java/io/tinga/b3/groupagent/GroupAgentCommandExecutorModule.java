package io.tinga.b3.groupagent;

import com.google.inject.AbstractModule;

public class GroupAgentCommandExecutorModule extends AbstractModule {

    private final GroupAgentCommand command;

    public GroupAgentCommandExecutorModule(GroupAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {

        bind(GroupAgentCommand.class).toInstance(this.command);

    }

}
