package io.tinga.belt.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.belt.GadgetFatalException;
import io.tinga.belt.Gadget;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.input.GadgetCommandOption;

public class CliCommandFactory implements GadgetCommandFactory {

    private static final Logger log = LoggerFactory.getLogger(CliCommandFactory.class);

    private final Map<GadgetCommandOption, Option> optionsCache;
    private final ObjectMapper om;
    private final CommandLineParser parser;
    private final CliCustomHelpFormatter formatter;

    @Inject
    public CliCommandFactory(ObjectMapper om, DefaultParser parser, CliCustomHelpFormatter formatter) {
        optionsCache = new HashMap<>();
        this.om = om;
        this.parser = parser;
        this.formatter = formatter;
    }

    @Override
    public <C extends Gadget.Command<?>> C parseArgs(Gadget<C> gadget, String[] args) throws GadgetFatalException {
        Options options = this.asOptions(gadget.commandOptions());
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(GadgetCommandOption.HELP_STANDARD_OPT)) {
                StringBuffer sb = formatter.renderOptions(new StringBuffer(), options);
                throw new GadgetFatalException(0, sb.toString());
            }

            JsonNode node = this.asJsonNode(gadget, cmd);
            C command = this.om.convertValue(node, gadget.commandClass());
            return command;

        } catch (ParseException e) {
            log.debug(String.format("Unable to parse args: %s", e.getMessage()));
            StringBuffer sb = formatter.renderOptions(new StringBuffer(), options);
            throw new GadgetFatalException(1, sb.toString());
        }
    }

    public JsonNode asJsonNode(Gadget<?> gadget, CommandLine cmd) {
        ObjectNode opts = JsonNodeFactory.instance.objectNode();

        for (GadgetCommandOption option : gadget.commandOptions()) {
            if (option.opt() == GadgetCommandOption.HELP_STANDARD_OPT) {
                continue;
            }

            if (option.opt() == GadgetCommandOption.POSITIONAL_ARGS_OPT) {
                ArrayNode positionals = JsonNodeFactory.instance.arrayNode();
                opts.set(GadgetCommandOption.POSITIONAL_ARGS_OPT, positionals);
                for (String item : cmd.getArgList()) {
                    positionals.add(item);
                }
                continue;
            }

            opts.put(option.opt(), cmd.getOptionValue(option.opt(), option.defaultValue()));
        }

        return opts;
    }

    public Option asOption(GadgetCommandOption option) {
        Option retval = this.optionsCache.get(option);
        if (retval == null) {
            retval = new Option(option.opt(), option.name().toLowerCase(), option.hasArg(), option.description());
            if (option.type() != null) {
                retval.setType(option.type());
            }
            this.optionsCache.put(option, retval);
        }
        return retval;
    }

    public Options asOptions(List<GadgetCommandOption> commandOptions) {
        Options options = new Options();
        for (GadgetCommandOption cliOption : commandOptions) {
            Option option = asOption(cliOption);
            if (option.getOpt() != GadgetCommandOption.POSITIONAL_ARGS_OPT) {
                options.addOption(option);
            }
        }
        return options;
    }

}
