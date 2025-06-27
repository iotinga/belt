package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface AgentTopic {

    interface Shadow {
        interface Reported extends TopicName {
            interface Live extends TopicName {
            }

            Live live();

            interface Batch extends TopicName {
            }

            Batch batch();

        }

        Reported reported();

        interface Desired {
            interface Role extends TopicName {
            }

            interface Batch {
                interface Role extends TopicName {
                }
            }

            Batch.Role batch(String role) throws TopicNameValidationException;
        }

        Desired desired();

        Desired.Role desired(String role) throws TopicNameValidationException;

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

