package io.tinga.b3.protocol.topic;

public interface TopicName {
    
    String DEFAULT_ROOT = "braid";
    String GLUE = "/";
    String RETAIN_PREFIX = "$retain";
    
    String build();
    String build(boolean retained);
}
