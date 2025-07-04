package io.tinga.b3.helpers.messageprovider;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.protocol.B3Message;

public class StdinB3MessageProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    private final static Logger log = LoggerFactory.getLogger(StdinB3MessageProvider.class);

    private final ObjectMapper om;
    private final Class<M> messageClass;

    @Inject
    public StdinB3MessageProvider(Class<M> messageClass, ObjectMapper om) {
        this.om = om;
        this.messageClass = messageClass;
    }

    @Override
    public M load(String messagePath) {
        try {
            return om.readValue(System.in, this.messageClass);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", messagePath, e.getMessage()));
            return null;
        }
    }
    
}
