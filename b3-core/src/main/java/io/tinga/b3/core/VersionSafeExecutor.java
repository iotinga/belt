package io.tinga.b3.core;

import java.util.function.Function;

import io.tinga.b3.protocol.topic.B3Topic;


public interface VersionSafeExecutor {

    @FunctionalInterface
    public interface CriticalSection extends Function<Function<Boolean, Integer>, Void> {
    }

    void initVersion(B3Topic topicName) throws AgentInitException;
    void safeExecute(CriticalSection versionCriticalSection) throws AgentInitException;
}