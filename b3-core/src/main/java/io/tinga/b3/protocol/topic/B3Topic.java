package io.tinga.b3.protocol.topic;

import java.util.List;

public interface B3Topic {
    
    String GLUE = "/";
    String RETAIN_PREFIX = "$retain";

    public String toString(boolean retained);
    public List<Token> tokens();
}
