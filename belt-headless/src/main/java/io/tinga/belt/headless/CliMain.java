package io.tinga.belt.headless;

import com.google.inject.Module;

import io.tinga.belt.cli.AbstractCli;

public class CliMain extends AbstractCli<HeadlessCommandExecutor, HeadlessCommand> {

    public CliMain(String[] args) {
        super(args, new HeadlessGadget());
    }

    @Override
    public Module[] buildRootModules() {
        Module[] retval = { new CliCustomRootModule() };
        return retval;
    }

    public static void main(String[] args) {
        new CliMain(args).run();
    }
}