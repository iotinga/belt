package io.tinga.belt.output;

import java.nio.charset.StandardCharsets;

import io.tinga.belt.helpers.Encoding;
import io.tinga.belt.helpers.MimeType;

public class GadgetInMemoryPlainTextSink extends AbstractGadgetInMemorySink {

    public static final String EMPTY_PAYLOAD = "[NULL]";

    @Override
    public MimeType contenType() {
        return MimeType.TEXT_PLAIN;
    }

    @Override
    public Encoding encoding() {
        return Encoding.UTF_8;
    }

    @Override
    public byte[] serialize(Object output) {
        byte[] cborData = output == null ? EMPTY_PAYLOAD.getBytes(StandardCharsets.UTF_8) : output.toString().getBytes(StandardCharsets.UTF_8);
        return cborData;
    }

}
