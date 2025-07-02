package io.tinga.b3.protocol.topic;

import com.google.inject.Inject;

import static io.tinga.b3.protocol.topic.B3Topic.DEFAULT_ROOT;
import static io.tinga.b3.protocol.topic.B3Topic.GLUE;

import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.topic.B3Topic.Category;

public class B3TopicFactoryImpl implements B3TopicFactory, B3TopicRoot {

    private final String root;


    @Inject
    public B3TopicFactoryImpl() {
        this.root = DEFAULT_ROOT;
    }

    public B3TopicFactoryImpl(String root) {
        String secureRoot = root == null ? DEFAULT_ROOT : root;
        this.root = secureRoot.endsWith(GLUE) ? secureRoot.substring(0,secureRoot.length()-1) : secureRoot;
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
}