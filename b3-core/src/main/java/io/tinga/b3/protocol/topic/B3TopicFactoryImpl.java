package io.tinga.b3.protocol.topic;

import com.google.inject.Inject;

import static io.tinga.b3.protocol.topic.B3Topic.GLUE;
import static io.tinga.b3.protocol.topic.B3TopicRoot.DEFAULT_ROOT;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.topic.B3TopicRoot.Category;

public class B3TopicFactoryImpl implements B3TopicFactory {

    private final String root;

    private record B3TopicRecord(List<Token> tokens) implements B3Topic {
        public String toString() {
            return tokens.stream().map(Token::value).collect(Collectors.joining(GLUE));
        }

        public String toString(boolean retained) {
            String prefix = retained ? B3Topic.RETAIN_PREFIX + GLUE : "";
            return prefix + toString();
        }
    }

    private static final Map<List<Token.Name>, BiFunction<B3TopicImpl, String, B3TopicImpl>> transitions = Map
            .ofEntries(
                    entry(List.of(Token.Name.AGENT, Token.Name.SHADOW), (e, t) -> (B3TopicImpl) e.shadow()),
                    entry(List.of(Token.Name.AGENT, Token.Name.COMMAND), (e, t) -> (B3TopicImpl) e.command()),
                    entry(List.of(Token.Name.ENTITY, Token.Name.SHADOW), (e, t) -> (B3TopicImpl) e.shadow()),
                    entry(List.of(Token.Name.ENTITY, Token.Name.COMMAND), (e, t) -> (B3TopicImpl) e.command()),
                    entry(List.of(Token.Name.SHADOW, Token.Name.DESIRED), (e, t) -> (B3TopicImpl) e.desired()),
                    entry(List.of(Token.Name.SHADOW, Token.Name.REPORTED), (e, t) -> (B3TopicImpl) e.reported()),
                    entry(List.of(Token.Name.REPORTED, Token.Name.LIVE), (e, t) -> (B3TopicImpl) e.live()),
                    entry(List.of(Token.Name.REPORTED, Token.Name.BATCH), (e, t) -> (B3TopicImpl) e.batch()),
                    entry(List.of(Token.Name.DESIRED, Token.Name.BATCH), (e, t) -> (B3TopicImpl) e.batch()),
                    entry(List.of(Token.Name.COMMAND, Token.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.command(t);
                    }),
                    entry(List.of(Token.Name.DESIRED, Token.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.desired(t);
                    }),
                    entry(List.of(Token.Name.BATCH, Token.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.batch(t);
                    }));

    private static Map.Entry<List<Token.Name>, BiFunction<B3TopicImpl, String, B3TopicImpl>> entry(
            List<Token.Name> key, BiFunction<B3TopicImpl, String, B3TopicImpl> value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private class B3TopicImpl implements B3TopicRoot, B3TopicRoot.Command, B3TopicRoot.Command.Role,
            B3TopicRoot.Shadow.Desired, B3TopicRoot.Shadow.Desired.Role, B3TopicRoot.Shadow.Desired.Batch,
            B3TopicRoot.Shadow.Desired.Batch.Role, B3TopicRoot.Shadow, B3TopicRoot.Shadow.Reported,
            B3TopicRoot.Shadow.Reported.Batch, B3TopicRoot.Shadow.Reported.Live {

        private final String id;
        protected final List<Token> stack;
        private final Category category;

        public B3TopicImpl(String root, Category category, String id) {
            this.id = id;
            this.category = category;
            this.stack = new ArrayList<>();
            this.stack.add(Token.from(Token.Name.ROOT, root));
            this.stack.add(Token.from(category));
            this.stack.add(Token.from(category, id));
        }

        @Override
        public Live live() {
            this.stack.add(Token.from(Token.Name.LIVE));
            return this;
        }

        @Override
        public Reported.Batch batch() {
            this.stack.add(Token.from(Token.Name.BATCH));
            return this;
        }

        @Override
        public Shadow shadow() {
            this.stack.add(Token.from(Token.Name.SHADOW));
            return this;
        }

        @Override
        public Command command() {
            this.stack.add(Token.from(Token.Name.COMMAND));
            return this;
        }

        @Override
        public Command.Role command(String role) {
            validate(role);
            this.stack.add(Token.from(Token.Name.COMMAND));
            this.stack.add(Token.from(Token.Name.ROLE_NAME, role));
            return this;
        }

        @Override
        public Reported reported() {
            this.stack.add(Token.from(Token.Name.REPORTED));
            return this;
        }

        @Override
        public Desired desired() {
            this.stack.add(Token.from(Token.Name.DESIRED));
            return this;
        }

        @Override
        public Desired.Role desired(String role) {
            validate(role);
            this.stack.add(Token.from(Token.Name.DESIRED));
            this.stack.add(Token.from(Token.Name.ROLE_NAME, role));
            return this;
        }

        @Override
        public Desired.Batch.Role batch(String role) {
            validate(role);
            this.stack.add(Token.from(Token.Name.BATCH));
            this.stack.add(Token.from(Token.Name.ROLE_NAME, role));
            return this;
        }

        public Token stepBack() {
            return this.stack.removeLast();
        }

        private void validate(String value) throws TopicNameValidationException {
            if (value.contains(B3Topic.GLUE))
                throw new TopicNameValidationException("invalid char");
        }

        @Override
        public B3Topic build() {
            return new B3TopicRecord(this.stack);
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public boolean isAnchestorOf(B3TopicRoot.Name topic) {
            return topic != null && topic.build().toString().startsWith(this.build().toString());
        }

        @Override
        public Category getCategory() {
            return this.category;
        }

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
    public B3TopicRoot agent(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new B3TopicImpl(this.root, Category.AGENT, id);
    }

    @Override
    public B3TopicRoot entity(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new B3TopicImpl(this.root, Category.ENTITY, id);
    }

    @Override
    public B3TopicRoot.Name parse(String topicPath) throws TopicNameValidationException {
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

        B3TopicImpl retval = new B3TopicImpl(parts[0], category, parts[2]);
        Token.Name prevToken = Token.Name.valueOf(category.name());

        for (int i = 3; i < parts.length; i++) {
            String raw = parts[i];
            Token.Name token;
            try {
                token = Token.Name.valueOf(raw.toUpperCase());
            } catch (Exception e) {
                token = Token.Name.ROLE_NAME;
            }

            List<Token.Name> key = List.of(prevToken, token);
            BiFunction<B3TopicImpl, String, B3TopicImpl> action = transitions.get(key);

            if (action == null)
                throw new TopicNameValidationException("Invalid transition: " + prevToken + " -> " + raw);

            String param = (token == Token.Name.ROLE_NAME) ? raw : null;
            retval = action.apply(retval, param);
            prevToken = token;
        }

        return retval;
    }
}