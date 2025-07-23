package io.tinga.belt.headless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import io.tinga.belt.cli.AbstractCli;
import io.tinga.belt.output.WriterPrintStream;

public class CliMain extends AbstractCli<HeadlessCommand> {

    private static final Logger log = LoggerFactory.getLogger(CliMain.class);

    public CliMain(String[] args) {
        super(args, new HeadlessGadget());
    }

    @Override
    public Module[] buildRootModules() {
        Module[] retval = { new CliCustomRootModule() };
        return retval;
    }

    public static void main(String[] args) {
        System.setErr(new WriterPrintStream(line -> {
            log.error(line);
            return null;
        }));
        System.setOut(new WriterPrintStream(line -> {
            log.info(line);
            return null;
        }));
        new CliMain(args).run();
    }
}