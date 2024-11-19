package io.tinga.belt.output;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import io.tinga.belt.helpers.Encoding;
import io.tinga.belt.helpers.MimeType;

public class GadgetInMemoryCborSink extends AbstractGadgetInMemorySink {

    private static final Logger log = LoggerFactory.getLogger(GadgetInMemoryCborSink.class);

    public static final byte[] EMPTY_MQTT_PAYLOAD = new byte[0];

    private final ObjectMapper om;

    public GadgetInMemoryCborSink() {
        this.om = new ObjectMapper(new CBORFactory());
    }

    @Override
    public MimeType contenType() {
        return MimeType.APPLICATION_CBOR;
    }

    @Override
    public Encoding encoding() {
        return Encoding.RAW_BYTES;
    }

    @Override
    public byte[] serialize(Object payload) {
        try {
            byte[] cborData = payload == null ? EMPTY_MQTT_PAYLOAD : this.om.writeValueAsBytes(payload);
            return cborData;
        } catch (JsonProcessingException e) {
            log.error(String.format("%s: can not encode payload", payload));
            return null;
        }   
    }
    
}
