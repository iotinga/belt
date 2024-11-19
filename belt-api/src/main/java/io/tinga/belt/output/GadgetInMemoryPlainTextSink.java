package io.tinga.belt.output;

import io.tinga.belt.helpers.Encoding;
import io.tinga.belt.helpers.MimeType;

public class GadgetInMemoryPlainTextSink extends AbstractGadgetInMemorySink {
    @Override
    public MimeType contenType() {
        return MimeType.TEXT_PLAIN;
    }

    @Override
    public Encoding encoding() {
        return Encoding.UTF_8;
    }

    @Override
    public byte[] serialize(Object payload) {
        if(payload != null) {
            return String.format("%s\n", payload.toString()).getBytes(this.encoding().getCharset());
        }
        return null;
    }
}
