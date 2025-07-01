package io.tinga.b3.core.agent;

import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.core.VersionSafeExecutor;

public abstract class AbstracVersionSafeExecutor implements VersionSafeExecutor {

    private Integer firstReportedVersion;
    private Integer currentReportedVersion;

    protected synchronized boolean initCurrentReportedVersion(int initValue) {
        if (!this.isInitialized()) {
            this.firstReportedVersion = initValue;
            this.currentReportedVersion = initValue;
            return true;
        }
        return false;
    }

    protected boolean isInitialized() {
        return this.firstReportedVersion != null;
    }

    @Override
    public final synchronized void safeExecute(CriticalSection versionCriticalSection) {
        versionCriticalSection.apply(this::getVersion);
    }

    private final Integer getVersion(boolean next) throws AgentInitException {
        if (this.currentReportedVersion == null) {
            throw new AgentInitException(
                    "Trying to execute critical version before version initialization: currentReportedVersion is null");
        }
        return next ? ++this.currentReportedVersion : this.currentReportedVersion;
    }

}