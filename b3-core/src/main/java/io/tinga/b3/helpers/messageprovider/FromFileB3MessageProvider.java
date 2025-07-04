package io.tinga.b3.helpers.messageprovider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.protocol.B3Message;

public class FromFileB3MessageProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    private final static Logger log = LoggerFactory.getLogger(FromFileB3MessageProvider.class);

    protected final ObjectMapper om;
    protected final Class<M> messageClass;

    @Inject
    public FromFileB3MessageProvider(Class<M> messageClass, ObjectMapper om) {
        this.om = om;
        this.messageClass = messageClass;
    }

    @Override
    public M load(String messagePath) {
        try {
            InputStream fis = this.getMessageInputStream(messagePath);
            return om.readValue(fis, messageClass);
        } catch (IOException e) {
            log.error(String.format("unable to load %s: %s", messagePath, e.getMessage()));
            return null;
        }
    }
    
    protected InputStream getMessageInputStream(String messagePath) throws IOException {
        return new FileInputStream(messagePath);
    }
}
