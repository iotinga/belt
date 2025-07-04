package io.tinga.b3.agent.cli;

import com.google.inject.Module;

import io.tinga.b3.agent.EntityAgentCommand;
import io.tinga.b3.agent.EntityAgentGadget;
import io.tinga.belt.cli.AbstractCli;

public class CliMain extends AbstractCli<EntityAgentCommand> {

    public CliMain(String[] args) {
        super(args, new EntityAgentGadget());
    }

    @Override
    public Module[] buildRootModules() {
        return AbstractCli.DEFAULT_ROOT_MODULES;
    }

    public static void main(String[] args) {
        new CliMain(args).run();
    }
}