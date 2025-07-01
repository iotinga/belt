package io.tinga.b3.protocol.topic;

import static io.tinga.b3.protocol.topic.TopicName.DEFAULT_ROOT;
import static io.tinga.b3.protocol.topic.TopicName.GLUE;

import com.google.inject.Inject;

import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.topic.B3Topic.Category;

public class BasicTopicNameFactory implements TopicNameFactory, B3TopicRoot {

    private final String root;

    @Inject
    public BasicTopicNameFactory() {
        this.root = DEFAULT_ROOT;
    }

    public BasicTopicNameFactory(String root) {
        String secureRoot = root == null ? DEFAULT_ROOT : root;
        this.root = secureRoot.endsWith(GLUE) ? secureRoot.substring(0,secureRoot.length()-1) : secureRoot;
    }

    @Override
    public B3TopicRoot root() {
        return this;
    }

    @Override
    public B3Topic topicName(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new TopicNameElement(this.root, Category.AGENT, id);
    }

    @Override
    public B3Topic entity(String id) {
        if (id.contains(GLUE)) {
            throw new TopicNameValidationException("invalid char");
        }
        return new TopicNameElement(this.root, Category.ENTITY, id);
    }
}