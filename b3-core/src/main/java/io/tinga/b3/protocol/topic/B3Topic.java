package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface B3Topic {

    String DEFAULT_ROOT = "braid";
    String GLUE = "/";
    String RETAIN_PREFIX = "$retain";

    public enum Category {
        AGENT,
        ENTITY
    }

    public interface Name {

        String build();

        String build(boolean retained);
    }

    interface Shadow {
        interface Reported extends Name {
            interface Live extends Name {
            }

            Live live();

            interface Batch extends Name {
            }

            Batch batch();

        }

        Reported reported();

        interface Desired {
            interface Role extends Name {
            }

            interface Batch {
                interface Role extends Name {
                }
            }

            Batch.Role batch(String role) throws TopicNameValidationException;
        }

        Desired desired();

        Desired.Role desired(String role) throws TopicNameValidationException;

    }

    interface Command extends Name {
        interface Role extends Name {
        }
    }

    boolean isAnchestorOf(String topic);

    String getId();

    Category getCategory();

    Shadow shadow();

    Command command();

    Command.Role command(String role) throws TopicNameValidationException;

}
