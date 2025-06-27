package io.tinga.b3.core.shadowing;

public interface ShadowDesiredPreProcessor<M> {
    void initialize();
    void inPlaceProcess(M incomingDesired);
}
