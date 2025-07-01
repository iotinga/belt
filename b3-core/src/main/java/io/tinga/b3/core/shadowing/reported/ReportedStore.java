package io.tinga.b3.core.shadowing.reported;

import java.util.concurrent.Future;

import io.tinga.b3.protocol.GenericB3Message;

public interface ReportedStore {
    public Future<Integer> init();
    public boolean isInitialized();
    public GenericB3Message read(String topicName);
    public GenericB3Message update(String topicName, GenericB3Message newValue);
}
