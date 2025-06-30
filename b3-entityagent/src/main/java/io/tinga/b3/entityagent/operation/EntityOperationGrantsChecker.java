package io.tinga.b3.entityagent.operation;

public interface EntityOperationGrantsChecker {
    public boolean isAllowed(EntityOperation operation);
}
