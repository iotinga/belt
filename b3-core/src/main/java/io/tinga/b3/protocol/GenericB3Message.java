package io.tinga.b3.protocol;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.tinga.belt.output.Status;

public class GenericB3Message extends B3Message<ObjectNode> {
    public GenericB3Message(Long timestamp, Integer version, Integer protocolVersion, String correlationId, Status status,
                        ObjectNode body) {
        super(timestamp, version, protocolVersion, correlationId, status, body);
    }
}
