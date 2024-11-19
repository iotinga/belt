package io.tinga.belt.output;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GadgetSystemDisplay implements GadgetDisplay {

    private static final Logger log = LoggerFactory.getLogger(GadgetSystemDisplay.class);

    private final StringBuilder buffer = new StringBuilder();
    private final GadgetSink output;

    @Inject
    public GadgetSystemDisplay(GadgetSink output) {
        this.output = output;
    }

    @Override
    public void run() {

        try {
            int data;

            while ((data = this.output.asStream().read()) != -1) {
                buffer.append((char) data);
                if (data == '\n') {
                    System.out.write(buffer.toString().getBytes());
                    System.out.flush();
                    buffer.setLength(0); // Clear the buffer
                }
            }
        } catch (IOException e) {

            log.warn(String.format("read failed: %s", e.getMessage()));

        } finally {
            try {
                // If there's remaining data in the buffer after the loop, write it out
                if (buffer.length() > 0) {
                    System.out.write(buffer.toString().getBytes());
                    System.out.flush();
                    buffer.setLength(0); // Clear the buffer
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public GadgetSink outputSink() {
        return this.output;
    }

    @Override
    public GadgetSink logSink() {
        return this.output;
    }
}
