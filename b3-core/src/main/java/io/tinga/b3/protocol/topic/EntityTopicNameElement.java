package io.tinga.b3.protocol.topic;

import java.util.ArrayList;
import java.util.List;

import io.tinga.b3.protocol.TopicNameValidationException;

class EntityTopicNameElement implements EntityTopic, EntityTopic.Shadow, EntityTopic.Shadow.Desired,
        EntityTopic.Shadow.Desired.Role, EntityTopic.Shadow.Reported, EntityTopic.Command, EntityTopic.Command.Role {

    private final String id;
    private final List<String> stack;

    public EntityTopicNameElement(String root, String id) {
        this.stack = new ArrayList<>();
        this.stack.add(root);
        this.stack.add("entity");
        this.stack.add(id);
        this.id = id;
    }

    @Override
    public Shadow shadow() {
        this.stack.add("shadow");
        return this;
    }

    @Override
    public Command command() {
        this.stack.add("command");
        return this;
    }

    @Override
    public Command.Role command(String role) {
        this.stack.add("command");
        if (role.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        this.stack.add(role);
        return this;
    }

    @Override
    public Desired desired() {
        this.stack.add("desired");
        return this;
    }

    @Override
    public Desired.Role desired(String role) {
        this.stack.add("desired");
        if (role.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        this.stack.add(role);
        return this;
    }

    @Override
    public Reported reported() {
        this.stack.add("reported");
        return this;
    }

    @Override
    public String build() {
        return String.join(GLUE, stack);
    }

    @Override
    public String build(boolean retained) {
        String prefix = retained ? RETAIN_PREFIX + GLUE : "";
        return prefix + String.join(GLUE, stack);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isAnchestorOf(String topic) {
        if (topic == null)
            return false;

        String current = this.build();
        return topic.startsWith(current);
    }

}
