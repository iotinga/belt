package io.tinga.b3.agent.shadowing;

import java.util.function.Function;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.protocol.B3Topic;


public interface VersionSafeExecutor {

    @FunctionalInterface
    public interface CriticalSection extends Function<Function<Boolean, Integer>, Void> {
    }

    void bind(B3Topic.Base topicBase, String roleName) throws InitializationException;
    void safeExecute(CriticalSection versionCriticalSection) throws InitializationException;
}