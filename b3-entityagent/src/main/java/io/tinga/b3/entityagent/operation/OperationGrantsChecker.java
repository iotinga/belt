package io.tinga.b3.entityagent.operation;

public interface OperationGrantsChecker {
    public boolean isAllowed(Operation operation);
}
