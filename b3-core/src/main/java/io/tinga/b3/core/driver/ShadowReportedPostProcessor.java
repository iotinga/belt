package io.tinga.b3.core.driver;

public interface ShadowReportedPostProcessor<M> {
    void initialize();
    void inPlaceProcess(M outcomingReported);
}
