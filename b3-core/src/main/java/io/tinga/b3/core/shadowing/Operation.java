package io.tinga.b3.core.shadowing;

import io.tinga.b3.core.InvalidOperationException;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public record Operation<M extends B3Message<?>>(B3Topic sourceTopic, M message) {

    public interface Factory {
        public <M extends B3Message<?>> Operation<M> buildFrom(String topic, M message)
                throws InvalidOperationException;

        // public <M extends B3Message<?>> Operation<M> buildFrom(B3TopicRoot.Name topic, M message)
        //         throws InvalidOperationException;

        public <M extends B3Message<?>> Operation<M> buildFrom(B3Topic topic, M message)
                throws InvalidOperationException;

    }

    public interface GrantsChecker<M extends B3Message<?>> {
        void bindTo(B3Topic.Root topicRoot);

        boolean isAllowed(Operation<M> operation);
    }

}
