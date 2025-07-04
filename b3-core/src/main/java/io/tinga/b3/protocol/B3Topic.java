package io.tinga.b3.protocol;

import java.util.List;

import io.tinga.b3.protocol.impl.B3TopicToken;

public interface B3Topic {

    String GLUE = "/";
    String RETAIN_PREFIX = "$retain";

    String DEFAULT_ROOT = "b3";

    public enum Category {
        AGENT,
        ENTITY;
    }

    public String toString(boolean retained);

    public List<B3TopicToken> tokens();

    public interface Factory {
        Valid parse(String topicPath) throws TopicNameValidationException;

        Root agent(String id) throws TopicNameValidationException;

        Root entity(String id) throws TopicNameValidationException;
    }

    public interface Valid {
        B3Topic build();
    }

    public interface Root {

        interface Shadow {
            interface Reported extends Valid {
                interface Live extends Valid {
                }

                Live live();

                interface Batch extends Valid {
                }

                Batch batch();

            }

            Reported reported();

            interface Desired {
                interface Role extends Valid {
                }

                interface Batch {
                    interface Role extends Valid {
                    }
                }

                Batch.Role batch(String role) throws TopicNameValidationException;
            }

            Desired desired();

            Desired.Role desired(String role) throws TopicNameValidationException;

        }

        interface Command extends Valid {
            interface Role extends Valid {
            }
        }

        boolean isRootOf(B3Topic topic);

        String getId();

        Category getCategory();

        Shadow shadow();

        Command command();

        Command.Role command(String role) throws TopicNameValidationException;

    }
}
