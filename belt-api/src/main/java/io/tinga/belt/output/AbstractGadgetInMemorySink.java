package io.tinga.belt.output;

import java.io.IOException;
import java.io.InputStream;

import io.tinga.belt.helpers.Encoding;
import io.tinga.belt.helpers.MimeType;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGadgetInMemorySink implements GadgetSink {
    private static final Logger log = LoggerFactory.getLogger(AbstractGadgetInMemorySink.class);

    private final PipedOutputStream output;
    private final PipedInputStream input;

    public AbstractGadgetInMemorySink() {
        this.output = new PipedOutputStream();
        PipedInputStream inputStream = null;
        try {
            inputStream = new PipedInputStream(this.output);
        } catch (IOException e) {
            log.error(String.format("broken pipe: %s", e.getMessage()));
        }

        this.input = inputStream == null ? new PipedInputStream() : inputStream;
    }

    @Override
    public void put(Object payload) {
        byte[] data = serialize(payload);
        if (data != null)
            try {
                this.output.write(data);
                this.output.flush();
            } catch (IOException e) {
                log.warn(String.format("%s", payload.toString()));
            }
    }

    @Override
    public void close() {
        try {
            this.output.close();
            this.input.close();
        } catch (IOException e) {
            log.error(String.format("unable to close: %s", e.getMessage()));
        }
    }

    @Override
    public InputStream asStream() {
        return this.input;
    }

    @Override
    abstract public MimeType contenType();

    @Override
    abstract public Encoding encoding();

    abstract public byte[] serialize(Object payload);
}
