package io.tinga.b3.entityagent.operation;

import io.tinga.b3.protocol.GenericB3Message;

public interface EntityOperationFactory {
    public EntityOperation buildFrom(String topic, GenericB3Message message) throws InvalidEntityOperationException;
}
