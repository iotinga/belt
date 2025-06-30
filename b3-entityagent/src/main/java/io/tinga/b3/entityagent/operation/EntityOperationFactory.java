package io.tinga.b3.entityagent.operation;

public interface EntityOperationFactory {
    public EntityOperation buildFrom(String topic, EntityMessage message) throws InvalidEntityOperationException;
}
