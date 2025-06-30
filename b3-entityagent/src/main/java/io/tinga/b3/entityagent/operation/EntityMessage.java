package io.tinga.b3.entityagent.operation;

import com.fasterxml.jackson.databind.JsonNode;

import io.tinga.belt.output.Status;
import io.tinga.b3.protocol.Action;
import io.tinga.b3.protocol.B3Message;

public class EntityMessage extends B3Message<JsonNode> {

    public EntityMessage() {
    }

    public EntityMessage(Long timestamp, Integer version, Integer protocolVersion, Action action, Status status,
            JsonNode body) {
        super(timestamp, version, protocolVersion, action, status, body);
    }

}
