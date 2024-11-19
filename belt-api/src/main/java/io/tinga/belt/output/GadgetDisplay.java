package io.tinga.belt.output;

public interface GadgetDisplay extends Runnable {
    public GadgetSink outputSink();
    public GadgetSink logSink();
}
