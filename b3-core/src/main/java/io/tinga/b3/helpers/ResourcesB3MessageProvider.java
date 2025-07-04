package io.tinga.b3.helpers;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.protocol.B3Message;

public class ResourcesB3MessageProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    private final static Logger log = LoggerFactory.getLogger(ResourcesB3MessageProvider.class);

    protected final ObjectMapper om;
    protected final Class<M> messageClass;

    @Inject
    public ResourcesB3MessageProvider(Class<M> messageClass, ObjectMapper om) {
        this.om = om;
        this.messageClass = messageClass;
    }

    @Override
    public M load(String desiredRef) {
        try {
            InputStream fis = ResourcesB3MessageProvider.class.getResourceAsStream(desiredRef);
            return om.readValue(fis, messageClass);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", desiredRef, e.getMessage()));
            return null;
        }
    }

}
