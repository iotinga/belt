package io.tinga.b3.entityagent.desired;

import io.tinga.b3.entityagent.operation.EntityMessage;

public interface DesiredEntityMessageProvider {
    public EntityMessage load(String desiredRef);
}
