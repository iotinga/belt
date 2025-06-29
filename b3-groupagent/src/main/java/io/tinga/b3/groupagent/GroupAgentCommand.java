package io.tinga.b3.groupagent;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.Gadget;

public class GroupAgentCommand implements Gadget.Command<GroupAgentAction> {

    public static final String ACTION = "a";
    public static final String SHADOW_PATH = "s";
    public static final String HELP = "h";

    @JsonProperty(SHADOW_PATH)
    private String shadowPath;

    @JsonProperty(ACTION)
    private GroupAgentAction action;

    @JsonProperty(HELP)
    private String help;

    public GroupAgentCommand() {}

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

    public GroupAgentAction action() {
        return this.action;
    }

}
