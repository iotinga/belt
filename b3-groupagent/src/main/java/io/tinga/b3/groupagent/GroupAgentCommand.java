package io.tinga.b3.groupagent;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.b3.core.Agent;

public class GroupAgentCommand implements Agent.Command {

    public static final String AGENT_ID = "a";
    public static final String ROLE_IN_MEMBERS = "u";
    public static final String MEMBERS_IDS = "m";
    public static final String SHADOW_DESIRED_PATH = "w";
    public static final String SHADOW_REPORTED_READ = "r";
    public static final String HELP = "h";

    @JsonProperty(AGENT_ID)
    private String agentId;

    @JsonProperty(ROLE_IN_MEMBERS)
    private String roleInMembers;

    @JsonProperty(MEMBERS_IDS)
    private String membersIds;

    @JsonProperty(SHADOW_DESIRED_PATH)
    private String shadowDesiredPath;

    @JsonProperty(SHADOW_REPORTED_READ)
    private Boolean shadowReportedRead;

    @JsonProperty(HELP)
    private String help;

    public GroupAgentCommand() {
    }

    public GroupAgentCommand(String agentId, String help, String roleInMembers, String members, String shadowDesiredPath, Boolean shadowReportedRead) {
        this.roleInMembers = roleInMembers;
        this.membersIds = members;
        this.agentId = agentId;
        this.help = help;
        this.shadowDesiredPath = shadowDesiredPath;
        this.shadowReportedRead = shadowReportedRead;
    }

    public String membersIds() {
        return this.membersIds;
    }

    public String roleInMembers() {
        return this.roleInMembers;
    }

    public String agentId() {
        return this.agentId;
    }

    public String help() {
        return this.help;
    }

    public String shadowDesiredPath() {
        return this.shadowDesiredPath;
    }

    public Boolean shadowReportedRead() {
        return this.shadowReportedRead;
    }

}
