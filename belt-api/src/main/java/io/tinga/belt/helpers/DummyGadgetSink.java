package io.tinga.belt.helpers;


import io.tinga.belt.output.AbstractGadgetInMemorySink;

public class DummyGadgetSink extends AbstractGadgetInMemorySink {


    @Override
    public MimeType contenType() {
        return MimeType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public Encoding encoding() {
        return Encoding.RAW_BYTES;
    }

    @Override
    public byte[] serialize(Object payload) {
        if (payload == null) {
            return null;
        }
        return payload.toString().getBytes();
    }

}
