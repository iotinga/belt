package io.tinga.b3.groupagent;

import java.util.List;

public interface GroupAgentConfig {

    String GROUPAGENT_MEMBERS_IDS_GLUE = ",";
    String GROUPAGENT_MEMBERS_IDS = "GROUP_MEMBERS_IDS";
    String GROUPAGENT_ROLE_IN_MEMBERS = "GROUP_AGENT_ROLE_IN_MEMBERS";

    String roleInMembers();
    String membersIds();
    List<String> members();
}