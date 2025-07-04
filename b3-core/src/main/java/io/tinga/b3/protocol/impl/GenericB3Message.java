package io.tinga.b3.protocol.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.tinga.b3.protocol.B3Message;
import io.tinga.belt.output.Status;

public class GenericB3Message extends B3Message<ObjectNode> {
    public GenericB3Message(Long timestamp, Integer version, Integer protocolVersion, String correlationId, Status status,
                        ObjectNode body) {
        super(timestamp, version, protocolVersion, correlationId, status, body);
    }
}
