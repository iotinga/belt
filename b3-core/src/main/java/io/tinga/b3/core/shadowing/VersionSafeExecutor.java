package io.tinga.b3.core.shadowing;

import java.util.function.Function;

import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.protocol.topic.B3TopicRoot;


public interface VersionSafeExecutor {

    @FunctionalInterface
    public interface CriticalSection extends Function<Function<Boolean, Integer>, Void> {
    }

    void initVersion(B3TopicRoot topicRoot) throws AgentInitException;
    void safeExecute(CriticalSection versionCriticalSection) throws AgentInitException;
}