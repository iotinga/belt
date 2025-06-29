package io.tinga.b3.groupagent;

import io.tinga.belt.input.GadgetCommandOption;

public enum GroupAgentCommandOption implements GadgetCommandOption {

    HELP(GroupAgentCommand.HELP, false, null, null, "Shows this guide"),
    ACTION(GroupAgentCommand.ACTION, true,GroupAgentAction.class, GroupAgentAction.SERVICE.name(), String.format("Defines the action to be performed between [SERVICE]. Defaults to: SERVICE")),
    SHADOW_PATH(GroupAgentCommand.SHADOW_PATH, true, String.class, null,
            "Path to the shadow you want to use as input (or output) of the command. It can be in JSON or CBOR formats");
            
    private final String opt;
    private final boolean hasArg;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;

    GroupAgentCommandOption(String opt, boolean hasArg, Class<?> type, String defaultValue,
            String description) {
        this.opt = opt;
        this.hasArg = hasArg;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @Override
    public String opt() {
        return opt;
    }

    @Override
    public boolean hasArg() {
        return hasArg;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

}
