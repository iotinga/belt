package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface EntityTopic {
    interface Shadow {
        interface Desired {
            interface Role extends TopicName {
            }
        }

        Desired desired();

        Desired.Role desired(String role) throws TopicNameValidationException;

        interface Reported extends TopicName {
        }

        Reported reported();
    }

    interface Command extends TopicName {
        interface Role extends TopicName {
        }
    }

    boolean isAnchestorOf(String topic);

    String getId();

    Shadow shadow();

    Command command();

    Command.Role command(String role) throws TopicNameValidationException;

}
