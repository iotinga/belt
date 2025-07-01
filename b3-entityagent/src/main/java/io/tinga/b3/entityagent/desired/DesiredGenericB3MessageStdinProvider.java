package io.tinga.b3.entityagent.desired;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.protocol.GenericB3Message;

public class DesiredGenericB3MessageStdinProvider implements DesiredGenericB3MessageProvider {

    private final static Logger log = LoggerFactory.getLogger(DesiredGenericB3MessageStdinProvider.class);

    @Inject
    private ObjectMapper om;

    @Override
    public GenericB3Message load(String desiredRef) {
        try {
            return om.readValue(System.in, GenericB3Message.class);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", desiredRef, e.getMessage()));
            return null;
        }
    }
    
}
