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
        Valid parse(String topicPath) throws B3InvalidTopicException;

        Base agent(String id) throws B3InvalidTopicException;

        Base entity(String id) throws B3InvalidTopicException;
    }

    public interface Valid {
        B3Topic build();
    }

    public interface Base {

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

                Batch.Role batch(String role) throws B3InvalidTopicException;
            }

            Desired desired();

            Desired.Role desired(String role) throws B3InvalidTopicException;

        }

        interface Command extends Valid {
            interface Role extends Valid {
            }
        }

        boolean isBaseOf(B3Topic topic);

        Shadow shadow();

        Command command();

        String id();

        Category category();

        String root();

        Command.Role command(String role) throws B3InvalidTopicException;

    }
}
