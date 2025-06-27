package io.tinga.b3.core.impl;

import io.tinga.b3.core.InitializationException;
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

    private final Integer getVersion(boolean next) throws InitializationException {
        if (this.currentReportedVersion == null) {
            throw new InitializationException(
                    "Trying to execute critical version before version initialization: currentReportedVersion is null");
        }
        return next ? ++this.currentReportedVersion : this.currentReportedVersion;
    }

}