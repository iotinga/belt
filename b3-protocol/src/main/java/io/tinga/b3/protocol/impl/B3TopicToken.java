package io.tinga.b3.protocol.impl;

import java.util.AbstractMap;
import java.util.Map;

import io.tinga.b3.protocol.B3Topic.Category;

public record B3TopicToken(Name name, String value) {

    enum Name {
        RETAIN, ROOT, AGENT, ENTITY, COMMAND, SHADOW, BATCH, LIVE, REPORTED, DESIRED, AGENT_ID, ENTITY_ID, ROLE_NAME;
    }

    private final static Map<B3TopicToken.Name, B3TopicToken> STATIC_TOKENS = Map.ofEntries(
            entry(Name.RETAIN),
            entry(Name.AGENT),
            entry(Name.ENTITY),
            entry(Name.COMMAND),
            entry(Name.SHADOW),
            entry(Name.BATCH),
            entry(Name.LIVE),
            entry(Name.REPORTED),
            entry(Name.DESIRED));

    private static Map.Entry<B3TopicToken.Name, B3TopicToken> entry(B3TopicToken.Name name) {
        return new AbstractMap.SimpleEntry<>(name, B3TopicToken.from(name, null));
    }

    public static B3TopicToken from(Name name) {
        return from(name, name.name().toLowerCase());
    }

    public static B3TopicToken from(Name name, String value) {
        B3TopicToken retval = null;
        if(STATIC_TOKENS != null) {
            retval = STATIC_TOKENS.get(name);
        }
        if (retval == null) {
            if (value == null) {
                retval = new B3TopicToken(name, name.name().toLowerCase());
            } else {
                retval = new B3TopicToken(name, value);
            }
        }

        return retval;
    }

    public static B3TopicToken from(Category category) {
        switch (category) {
            case ENTITY:
                return from(Name.ENTITY, category.name());
            case AGENT:
            default:
                return from(Name.AGENT, category.name());
        }
    }

    public static B3TopicToken from(Category category, String value) {
        switch (category) {
            case ENTITY:
                return from(Name.ENTITY_ID, value);
            case AGENT:
            default:
                return from(Name.AGENT_ID, value);
        }
    }

}
