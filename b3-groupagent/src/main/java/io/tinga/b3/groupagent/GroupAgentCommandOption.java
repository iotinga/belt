package io.tinga.b3.groupagent;

import io.tinga.belt.input.GadgetCommandOption;

public enum GroupAgentCommandOption implements GadgetCommandOption {

    HELP(GroupAgentCommand.HELP, false, null, null, "Shows this guide"),
    AGENT(GroupAgentCommand.AGENT_ID, true, String.class, "line",
            "The agent id used to identify the agent in the B3 protocol"),
    MEMBERS(GroupAgentCommand.MEMBERS_IDS, true, String.class, "agent1,agent2",
            "A comma-separated list, without spaces, of group members agent ids"),
    SHADOW_DESIRED_PATH(GroupAgentCommand.SHADOW_DESIRED_PATH, true, String.class, null,
            "Path to the shadow desired you want to publish to the group. It can be in JSON or CBOR formats"),
    SHADOW_REPORTED_READ(GroupAgentCommand.SHADOW_REPORTED_READ, false, Boolean.class, null,
            "Forces a read of the shadow reported from the group. If a shadow desired path is specified, it reads the shadow before the shadow desired write attempt"),
    ROLE_IN_MEMBERS(GroupAgentCommand.ROLE_IN_MEMBERS, true, String.class, "admin",
            "The role that the group manager shall use to publish desired shadows to its members");

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
