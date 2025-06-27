package io.tinga.b3.core.shadowing;

public interface ShadowReportedPostProcessor<M> {
    void initialize();
    void inPlaceProcess(M outcomingReported);
}
