package io.tinga.b3.agent.security.impl;

import com.fasterxml.jackson.databind.JsonNode;

import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic.Base;

public class DisabledGrantsChecker<M extends B3Message<? extends JsonNode>>
        implements Operation.GrantsChecker<M> {

    @Override
    public void bind(Base topicBase, String roleName) {
        // NOTHING TO DO
    }

    @Override
    public boolean isAllowed(Operation<M> operation) {
        return true;
    }

}
