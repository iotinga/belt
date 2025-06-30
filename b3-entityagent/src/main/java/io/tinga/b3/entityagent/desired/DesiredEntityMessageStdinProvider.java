package io.tinga.b3.entityagent.desired;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.entityagent.operation.EntityMessage;

public class DesiredEntityMessageStdinProvider implements DesiredEntityMessageProvider {

    private final static Logger log = LoggerFactory.getLogger(DesiredEntityMessageStdinProvider.class);

    @Inject
    private ObjectMapper om;

    @Override
    public EntityMessage load(String desiredRef) {
        try {
            return om.readValue(System.in, EntityMessage.class);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", desiredRef, e.getMessage()));
            return null;
        }
    }
    
}
