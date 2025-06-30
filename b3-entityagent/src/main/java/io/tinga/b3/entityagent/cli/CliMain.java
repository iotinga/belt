package io.tinga.b3.entityagent.cli;

import com.google.inject.Module;

import io.tinga.belt.cli.AbstractCli;
import io.tinga.b3.entityagent.EntityCommand;
import io.tinga.b3.entityagent.EntityGadget;

public class CliMain extends AbstractCli<EntityCommand> {

    public CliMain(String[] args) {
        super(args, new EntityGadget());
    }

    @Override
    public Module[] buildRootModules() {
        return AbstractCli.DEFAULT_ROOT_MODULES;
    }

    public static void main(String[] args) {
        new CliMain(args).run();
    }
}