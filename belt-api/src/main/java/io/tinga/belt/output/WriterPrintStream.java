package io.tinga.belt.output;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Function;

public class WriterPrintStream extends PrintStream {

    private static class WriterOutputStream extends OutputStream {
        private final Function<String, Void> writer;
        private final StringBuilder buffer = new StringBuilder();

        public WriterOutputStream(Function<String, Void> writer) {
            this.writer = writer;
        }

        @Override
        public void write(int b) {
            char c = (char) b;
            if (c == '\n') {
                flushBuffer();
            } else {
                buffer.append(c);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) {
            for (int i = off; i < off + len; i++) {
                write(b[i]);
            }
        }

        private void flushBuffer() {
            if (buffer.length() > 0) {
                writer.apply(buffer.toString());
                buffer.setLength(0);
            }
        }

        @Override
        public void flush() {
            flushBuffer();
        }
    }

    public WriterPrintStream(Function<String, Void> writer) {
        super(new WriterOutputStream(writer), true); // autoFlush = true
    }
}