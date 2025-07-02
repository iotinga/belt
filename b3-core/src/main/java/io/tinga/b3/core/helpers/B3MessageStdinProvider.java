package io.tinga.b3.core.helpers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.protocol.B3Message;

public class B3MessageStdinProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    private final static Logger log = LoggerFactory.getLogger(B3MessageStdinProvider.class);

    private final ObjectMapper om;
    private final Class<M> messageClass;

    @Inject
    public B3MessageStdinProvider(Class<M> messageClass, ObjectMapper om) {
        this.om = om;
        this.messageClass = messageClass;
    }

    @Override
    public M load(String desiredRef) {
        try {
            return om.readValue(System.in, this.messageClass);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", desiredRef, e.getMessage()));
            return null;
        }
    }
    
}
