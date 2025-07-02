package io.tinga.b3.core.helpers;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.protocol.B3Message;

public class B3MessageResourcesProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    private final static Logger log = LoggerFactory.getLogger(B3MessageResourcesProvider.class);

    protected final ObjectMapper om;
    protected final Class<M> messageClass;

    @Inject
    public B3MessageResourcesProvider(Class<M> messageClass, ObjectMapper om) {
        this.om = om;
        this.messageClass = messageClass;
    }

    @Override
    public M load(String desiredRef) {
        try {
            InputStream fis = B3MessageResourcesProvider.class.getResourceAsStream(desiredRef);
            return om.readValue(fis, messageClass);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", desiredRef, e.getMessage()));
            return null;
        }
    }

}
