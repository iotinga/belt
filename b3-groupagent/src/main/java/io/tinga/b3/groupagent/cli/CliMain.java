package io.tinga.b3.groupagent.cli;

import com.google.inject.Module;

import io.tinga.b3.groupagent.GroupAgentCommand;
import io.tinga.b3.groupagent.GroupAgentCommandExecutor;
import io.tinga.b3.groupagent.GroupAgentGadget;
import io.tinga.belt.cli.AbstractCli;

public class CliMain extends AbstractCli<GroupAgentCommandExecutor, GroupAgentCommand> {

    public CliMain(String[] args) {
        super(args, new GroupAgentGadget());
    }

    @Override
    public Module[] buildRootModules() {
        return AbstractCli.DEFAULT_ROOT_MODULES;
    }

    public static void main(String[] args) {
        new CliMain(args).run();
    }
}