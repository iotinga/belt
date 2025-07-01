package io.tinga.b3.protocol.topic;

import java.util.ArrayList;
import java.util.List;

import io.tinga.b3.protocol.TopicNameValidationException;

class B3TopicNameElement implements B3Topic, B3Topic.Command, B3Topic.Command.Role,
        B3Topic.Shadow.Desired, B3Topic.Shadow.Desired.Role, B3Topic.Shadow.Desired.Batch,
        B3Topic.Shadow.Desired.Batch.Role, B3Topic.Shadow, B3Topic.Shadow.Reported,
        B3Topic.Shadow.Reported.Batch, B3Topic.Shadow.Reported.Live {

    private final String id;
    private final List<String> stack;
    private final Category category;

    public B3TopicNameElement(String root, Category category, String id) {
        this.id = id;
        this.category = category;
        this.stack = new ArrayList<>();
        this.stack.add(root);
        this.stack.add(category.name().toLowerCase());
        this.stack.add(id);
    }

    @Override
    public Live live() {
        this.stack.add("live");
        return this;
    }

    @Override
    public Reported.Batch batch() {
        this.stack.add("batch");
        return this;
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
    public Reported reported() {
        this.stack.add("reported");
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
    public Desired.Batch.Role batch(String role) {
        this.stack.add("batch");
        if (role.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        this.stack.add(role);
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

    @Override
    public Category getCategory() {
        return this.category;
    }

}