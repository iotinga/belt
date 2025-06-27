package io.tinga.b3.groupagent;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupAgentConfigImpl implements GroupAgentConfig {

    private List<String> members;

    @JsonProperty(GROUPAGENT_MEMBERS_IDS)
    private String membersIds;

    @JsonProperty(GROUPAGENT_ROLE_IN_MEMBERS)
    private String roleInMembers;

    @Override
    public String membersIds() {
        return membersIds;
    }

    @Override
    public List<String> members() {
        if (members == null) {
            if (membersIds != null && membersIds.trim().length() > 0) {
                this.members = List.of(membersIds.trim().split(GROUPAGENT_MEMBERS_IDS_GLUE));
            } else {
                this.members = new ArrayList<>();
            }
        }
        return this.members;
    }

    @Override
    public String roleInMembers() {
        return this.roleInMembers;
    }

}
