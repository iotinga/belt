package io.tinga.b3.protocol.topic;

import java.util.AbstractMap;
import java.util.Map;

import io.tinga.b3.protocol.topic.B3TopicRoot.Category;

public record Token(Name name, String value) {

    enum Name {
        RETAIN, ROOT, AGENT, ENTITY, COMMAND, SHADOW, BATCH, LIVE, REPORTED, DESIRED, AGENT_ID, ENTITY_ID, ROLE_NAME;
    }

    private final static Map<Token.Name, Token> staticTokens = Map.ofEntries(
            entry(Name.RETAIN),
            entry(Name.AGENT),
            entry(Name.ENTITY),
            entry(Name.COMMAND),
            entry(Name.SHADOW),
            entry(Name.BATCH),
            entry(Name.LIVE),
            entry(Name.REPORTED),
            entry(Name.DESIRED));

    private static Map.Entry<Token.Name, Token> entry(Token.Name name) {
        return new AbstractMap.SimpleEntry<>(name, new Token(name, name.name()));
    }

    public static Token from(Name name) {
        return from(name, name.name());
    }

    public static Token from(Name name, String value) {
        Token retval = staticTokens.get(name);
        if (retval == null) {
            if (value == null) {
                new Token(name, name.name().toLowerCase());
            } else {
                retval = new Token(name, value);
            }
        }

        return retval;
    }

    public static Token from(Category category) {
        switch (category) {
            case ENTITY:
                return from(Name.ENTITY, category.name());
            case AGENT:
            default:
                return from(Name.AGENT, category.name());
        }
    }

    public static Token from(Category category, String value) {
        switch (category) {
            case ENTITY:
                return from(Name.ENTITY_ID, value);
            case AGENT:
            default:
                return from(Name.AGENT_ID, value);
        }
    }

}
