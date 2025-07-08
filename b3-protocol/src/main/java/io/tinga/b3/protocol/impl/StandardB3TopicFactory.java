package io.tinga.b3.protocol.impl;

import com.google.inject.Inject;

import static io.tinga.b3.protocol.B3Topic.GLUE;
import static io.tinga.b3.protocol.B3Topic.DEFAULT_ROOT;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3InvalidTopicException;
import io.tinga.b3.protocol.B3Topic.Category;

public class StandardB3TopicFactory implements B3Topic.Factory {

    private final String root;

    protected record B3TopicRecord(List<B3TopicToken> tokens) implements B3Topic {
        public String toString() {
            return tokens.stream().map(B3TopicToken::value).collect(Collectors.joining(GLUE));
        }

        public String toString(boolean retained) {
            String prefix = retained ? B3Topic.RETAIN_PREFIX + GLUE : "";
            return prefix + toString();
        }
    }

    private static final Map<List<B3TopicToken.Name>, BiFunction<B3TopicImpl, String, B3TopicImpl>> transitions = Map
            .ofEntries(
                    entry(List.of(B3TopicToken.Name.AGENT, B3TopicToken.Name.SHADOW),
                            (e, t) -> (B3TopicImpl) e.shadow()),
                    entry(List.of(B3TopicToken.Name.AGENT, B3TopicToken.Name.COMMAND),
                            (e, t) -> (B3TopicImpl) e.command()),
                    entry(List.of(B3TopicToken.Name.ENTITY, B3TopicToken.Name.SHADOW),
                            (e, t) -> (B3TopicImpl) e.shadow()),
                    entry(List.of(B3TopicToken.Name.ENTITY, B3TopicToken.Name.COMMAND),
                            (e, t) -> (B3TopicImpl) e.command()),
                    entry(List.of(B3TopicToken.Name.SHADOW, B3TopicToken.Name.DESIRED),
                            (e, t) -> (B3TopicImpl) e.desired()),
                    entry(List.of(B3TopicToken.Name.SHADOW, B3TopicToken.Name.REPORTED),
                            (e, t) -> (B3TopicImpl) e.reported()),
                    entry(List.of(B3TopicToken.Name.REPORTED, B3TopicToken.Name.LIVE),
                            (e, t) -> (B3TopicImpl) e.live()),
                    entry(List.of(B3TopicToken.Name.REPORTED, B3TopicToken.Name.BATCH),
                            (e, t) -> (B3TopicImpl) e.batch()),
                    entry(List.of(B3TopicToken.Name.DESIRED, B3TopicToken.Name.BATCH),
                            (e, t) -> (B3TopicImpl) e.batch()),
                    entry(List.of(B3TopicToken.Name.COMMAND, B3TopicToken.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.command(t);
                    }),
                    entry(List.of(B3TopicToken.Name.DESIRED, B3TopicToken.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.desired(t);
                    }),
                    entry(List.of(B3TopicToken.Name.BATCH, B3TopicToken.Name.ROLE_NAME), (e, t) -> {
                        e.stepBack();
                        return (B3TopicImpl) e.batch(t);
                    }));

    private static Map.Entry<List<B3TopicToken.Name>, BiFunction<B3TopicImpl, String, B3TopicImpl>> entry(
            List<B3TopicToken.Name> key, BiFunction<B3TopicImpl, String, B3TopicImpl> value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private class B3TopicImpl implements B3Topic.Base, B3Topic.Base.Command, B3Topic.Base.Command.Role,
            B3Topic.Base.Shadow.Desired, B3Topic.Base.Shadow.Desired.Role, B3Topic.Base.Shadow.Desired.Batch,
            B3Topic.Base.Shadow.Desired.Batch.Role, B3Topic.Base.Shadow, B3Topic.Base.Shadow.Reported,
            B3Topic.Base.Shadow.Reported.Batch, B3Topic.Base.Shadow.Reported.Live {

        protected final List<B3TopicToken> stack;

        private final String id;
        private final Category category;
        private final String root;

        public B3TopicImpl(String root, Category category, String id) {
            this.root = root;
            this.category = category;
            this.id = id;
            this.stack = new ArrayList<>();
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.ROOT, root));
            this.stack.add(B3TopicToken.from(category));
            this.stack.add(B3TopicToken.from(category, id));
        }

        @Override
        public Live live() {
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.LIVE));
            return this;
        }

        @Override
        public Reported.Batch batch() {
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.BATCH));
            return this;
        }

        @Override
        public Shadow shadow() {
            B3TopicImpl retval = new B3TopicImpl(this.root, this.category, this.id);
            retval.stack.add(B3TopicToken.from(B3TopicToken.Name.SHADOW));
            return retval;
        }

        @Override
        public Command command() {
            B3TopicImpl retval = new B3TopicImpl(this.root, this.category, this.id);
            retval.stack.add(B3TopicToken.from(B3TopicToken.Name.COMMAND));
            return retval;
        }

        @Override
        public Command.Role command(String role) {
            validate(role);
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.COMMAND));
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.ROLE_NAME, role));
            return this;
        }

        @Override
        public Reported reported() {
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.REPORTED));
            return this;
        }

        @Override
        public Desired desired() {
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.DESIRED));
            return this;
        }

        @Override
        public Desired.Role desired(String role) {
            validate(role);
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.DESIRED));
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.ROLE_NAME, role));
            return this;
        }

        @Override
        public Desired.Batch.Role batch(String role) {
            validate(role);
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.BATCH));
            this.stack.add(B3TopicToken.from(B3TopicToken.Name.ROLE_NAME, role));
            return this;
        }

        public B3TopicToken stepBack() {
            return this.stack.removeLast();
        }

        private void validate(String value) throws B3InvalidTopicException {
            if (value.contains(B3Topic.GLUE))
                throw new B3InvalidTopicException("invalid char");
        }

        @Override
        public B3Topic build() {
            return new B3TopicRecord(List.copyOf(this.stack));
        }

        @Override
        public boolean isBaseOf(B3Topic topic) {
            return topic != null && topic.toString().startsWith(this.build().toString());
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public Category category() {
            return category;
        }

        @Override
        public String root() {
            return root;
        }

    }

    @Inject
    public StandardB3TopicFactory() {
        this.root = DEFAULT_ROOT;
    }

    public StandardB3TopicFactory(String root) {
        String secureRoot = root == null ? DEFAULT_ROOT : root;
        this.root = secureRoot.endsWith(GLUE) ? secureRoot.substring(0, secureRoot.length() - 1) : secureRoot;
    }

    @Override
    public B3Topic.Base agent(String id) {
        if (id.contains(GLUE)) {
            throw new B3InvalidTopicException("invalid char");
        }
        return new B3TopicImpl(this.root, Category.AGENT, id);
    }

    @Override
    public B3Topic.Base entity(String id) {
        if (id.contains(GLUE)) {
            throw new B3InvalidTopicException("invalid char");
        }
        return new B3TopicImpl(this.root, Category.ENTITY, id);
    }

    @Override
    public B3Topic.Valid parse(String topicPath) throws B3InvalidTopicException {
        if (topicPath == null || topicPath.trim().isEmpty())
            throw new B3InvalidTopicException("The given topicPath is null or empty");

        String[] parts = topicPath.split(GLUE);
        if (parts.length < 4)
            throw new B3InvalidTopicException("The given topicPath is too short: " + topicPath);

        Category category;
        try {
            category = Category.valueOf(parts[1].toUpperCase());
        } catch (Exception e) {
            throw new B3InvalidTopicException("Invalid category in topicPath: " + topicPath);
        }

        B3TopicImpl retval = new B3TopicImpl(parts[0], category, parts[2]);
        B3TopicToken.Name prevToken = B3TopicToken.Name.valueOf(category.name());

        for (int i = 3; i < parts.length; i++) {
            String raw = parts[i];
            B3TopicToken.Name token;
            try {
                token = B3TopicToken.Name.valueOf(raw.toUpperCase());
            } catch (Exception e) {
                token = B3TopicToken.Name.ROLE_NAME;
            }

            List<B3TopicToken.Name> key = List.of(prevToken, token);
            BiFunction<B3TopicImpl, String, B3TopicImpl> action = transitions.get(key);

            if (action == null)
                throw new B3InvalidTopicException("Invalid transition: " + prevToken + " -> " + raw);

            String param = (token == B3TopicToken.Name.ROLE_NAME) ? raw : null;
            retval = action.apply(retval, param);
            prevToken = token;
        }

        return retval;
    }
}