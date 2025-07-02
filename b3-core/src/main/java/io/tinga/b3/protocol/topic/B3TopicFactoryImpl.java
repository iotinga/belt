package io.tinga.b3.protocol.topic;

import com.google.inject.Inject;

import static io.tinga.b3.protocol.topic.B3Topic.DEFAULT_ROOT;
import static io.tinga.b3.protocol.topic.B3Topic.GLUE;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.topic.B3Topic.Category;

public class B3TopicFactoryImpl implements B3TopicFactory, B3TopicRoot {

    private final String root;

    private static final Map<List<Token>, BiFunction<B3TopicNameElement, String, B3TopicNameElement>> transitions = Map
            .ofEntries(
                    entry(List.of(Token.AGENT, Token.SHADOW), (t, v) -> (B3TopicNameElement) t.shadow()),
                    entry(List.of(Token.AGENT, Token.COMMAND), (t, v) -> (B3TopicNameElement) t.command()),
                    entry(List.of(Token.ENTITY, Token.SHADOW), (t, v) -> (B3TopicNameElement) t.shadow()),
                    entry(List.of(Token.ENTITY, Token.COMMAND), (t, v) -> (B3TopicNameElement) t.command()),
                    entry(List.of(Token.SHADOW, Token.DESIRED), (t, v) -> (B3TopicNameElement) t.desired()),
                    entry(List.of(Token.SHADOW, Token.REPORTED), (t, v) -> (B3TopicNameElement) t.reported()),
                    entry(List.of(Token.REPORTED, Token.LIVE), (t, v) -> (B3TopicNameElement) t.live()),
                    entry(List.of(Token.REPORTED, Token.BATCH), (t, v) -> (B3TopicNameElement) t.batch()),
                    entry(List.of(Token.DESIRED, Token.BATCH), (t, v) -> (B3TopicNameElement) t.batch()),
                    entry(List.of(Token.COMMAND, Token.ROLE_NAME), (t, v) -> {
                        t.stepBack();
                        return (B3TopicNameElement) t.command(v);
                    }),
                    entry(List.of(Token.DESIRED, Token.ROLE_NAME), (t, v) -> {
                        t.stepBack();
                        return (B3TopicNameElement) t.desired(v);
                    }),
                    entry(List.of(Token.BATCH, Token.ROLE_NAME), (t, v) -> {
                        t.stepBack();
                        return (B3TopicNameElement) t.batch(v);
                    }));

    private static Map.Entry<List<Token>, BiFunction<B3TopicNameElement, String, B3TopicNameElement>> entry(
            List<Token> key, BiFunction<B3TopicNameElement, String, B3TopicNameElement> value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    @Inject
    public B3TopicFactoryImpl() {
        this.root = DEFAULT_ROOT;
    }

    public B3TopicFactoryImpl(String root) {
        String secureRoot = root == null ? DEFAULT_ROOT : root;
        this.root = secureRoot.endsWith(GLUE) ? secureRoot.substring(0, secureRoot.length() - 1) : secureRoot;
    }

    @Override
    public B3TopicRoot root() {
        return this;
    }

    @Override
    public B3Topic agent(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new B3TopicNameElement(this.root, Category.AGENT, id);
    }

    @Override
    public B3Topic entity(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new B3TopicNameElement(this.root, Category.ENTITY, id);
    }

    @Override
    public B3Topic.Name parse(String topicPath) throws TopicNameValidationException {
        if (topicPath == null || topicPath.trim().isEmpty())
            throw new TopicNameValidationException("The given topicPath is null or empty");

        String[] parts = topicPath.split(GLUE);
        if (parts.length < 4)
            throw new TopicNameValidationException("The given topicPath is too short: " + topicPath);

        Category category;
        try {
            category = Category.valueOf(parts[1].toUpperCase());
        } catch (Exception e) {
            throw new TopicNameValidationException("Invalid category in topicPath: " + topicPath);
        }

        B3TopicNameElement retval = new B3TopicNameElement(parts[0], category, parts[2]);
        Token prevToken = Token.valueOf(category.name());

        for (int i = 3; i < parts.length; i++) {
            String raw = parts[i];
            Token token;
            try {
                token = Token.valueOf(raw.toUpperCase());
            } catch (Exception e) {
                token = Token.ROLE_NAME;
            }

            List<Token> key = List.of(prevToken, token);
            BiFunction<B3TopicNameElement, String, B3TopicNameElement> action = transitions.get(key);

            if (action == null)
                throw new TopicNameValidationException("Invalid transition: " + prevToken + " -> " + raw);

            String param = (token == Token.ROLE_NAME) ? raw : null;
            retval = action.apply(retval, param);
            prevToken = token;
        }

        return retval;
    }
}