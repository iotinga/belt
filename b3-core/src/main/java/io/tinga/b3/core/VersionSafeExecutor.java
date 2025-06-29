package io.tinga.b3.core;

import java.util.function.Function;

import io.tinga.b3.protocol.topic.AgentTopic;


public interface VersionSafeExecutor {

    @FunctionalInterface
    public interface CriticalSection extends Function<Function<Boolean, Integer>, Void> {
    }

    void initVersion(AgentTopic agentTopic) throws AgentInitException;
    void safeExecute(CriticalSection versionCriticalSection) throws AgentInitException;
}