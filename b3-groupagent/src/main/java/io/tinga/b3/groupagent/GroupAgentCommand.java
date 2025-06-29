package io.tinga.b3.groupagent;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupAgentCommand {

    public static final String ACTION = "a";
    public static final String SHADOW_PATH = "s";
    public static final String HELP = "h";

    @JsonProperty(SHADOW_PATH)
    private String shadowPath;

    @JsonProperty(ACTION)
    private GroupAgentCommandAction action;

    @JsonProperty(HELP)
    private String help;

    public GroupAgentCommand() {
    }

    public GroupAgentCommand(String help, String shadowPath) {
        this.help = help;
        this.shadowPath = shadowPath;
    }

    public String help() {
        return this.help;
    }

    public String shadowPath() {
        return this.shadowPath;
    }

    public GroupAgentCommandAction action() {
        return this.action;
    }

}
