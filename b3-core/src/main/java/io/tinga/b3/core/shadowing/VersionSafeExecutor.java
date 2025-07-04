package io.tinga.b3.core.shadowing;

import java.util.function.Function;

import io.tinga.b3.core.InitializationException;
import io.tinga.b3.protocol.B3Topic;


public interface VersionSafeExecutor {

    @FunctionalInterface
    public interface CriticalSection extends Function<Function<Boolean, Integer>, Void> {
    }

    void initVersion(B3Topic.Root topicRoot) throws InitializationException;
    void safeExecute(CriticalSection versionCriticalSection) throws InitializationException;
}