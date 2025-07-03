package io.tinga.b3.core.driver;

public interface ShadowDesiredPreProcessor<M> {
    void initialize();
    void inPlaceProcess(M incomingDesired);
}
