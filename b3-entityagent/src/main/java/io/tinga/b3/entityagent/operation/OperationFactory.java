package io.tinga.b3.entityagent.operation;

import io.tinga.b3.protocol.GenericB3Message;

public interface OperationFactory {
    public Operation buildFrom(String topic, GenericB3Message message) throws InvalidOperationException;
}
