package io.tinga.belt.output;

import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import java.io.IOException;

import org.slf4j.Logger;

public class GadgetSlf4jDisplay implements GadgetDisplay {

    private static final Logger out = LoggerFactory.getLogger(GadgetDisplay.class);
    private static final Logger log = LoggerFactory.getLogger(GadgetSlf4jDisplay.class);

    private final StringBuilder buffer = new StringBuilder();
    private final GadgetSink output;

    @Inject
    public GadgetSlf4jDisplay(GadgetSink output) {
        this.output = output;
    }

    @Override
    public GadgetSink outputSink() {
        return this.output;
    }

    @Override
    public void run() {
        try {
            int data;
            while ((data = this.output.asStream().read()) != -1) {
                buffer.append((char) data);
                if (data == '\n') {
                    out.info(buffer.toString());
                    buffer.setLength(0); // Clear the buffer
                }
            }

            // If there's remaining data in the buffer after the loop, write it out
            if (buffer.length() > 0) {
                log.info(buffer.toString());
                buffer.setLength(0); // Clear the buffer
            }
        } catch (IOException e) {
            log.warn(String.format("read failed: %s", e.getMessage()));
        }
    }

    @Override
    public GadgetSink logSink() {
        return this.output;
    }
}