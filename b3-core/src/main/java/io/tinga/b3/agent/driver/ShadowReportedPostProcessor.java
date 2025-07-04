package io.tinga.b3.agent.driver;

public interface ShadowReportedPostProcessor<M> {
    void initialize();
    void inPlaceProcess(M outcomingReported);
}
