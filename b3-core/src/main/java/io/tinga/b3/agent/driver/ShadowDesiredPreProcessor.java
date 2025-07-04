package io.tinga.b3.agent.driver;

public interface ShadowDesiredPreProcessor<M> {
    void initialize();
    void inPlaceProcess(M incomingDesired);
}
