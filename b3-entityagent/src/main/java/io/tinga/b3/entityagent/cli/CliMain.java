package io.tinga.b3.entityagent.cli;

import com.google.inject.Module;

import io.tinga.belt.cli.AbstractCli;
import io.tinga.b3.entityagent.EntityAgentCommand;
import io.tinga.b3.entityagent.EntityAgentGadget;

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