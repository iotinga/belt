package io.tinga.b3.entityagent.reported;

import java.util.concurrent.Future;

import io.tinga.b3.entityagent.operation.EntityMessage;

public interface ReportedStore {
    public Future<Integer> init();
    public boolean isInitialized();
    public EntityMessage read(String topicName);
    public EntityMessage update(String topicName, EntityMessage newValue);
}
