package io.tinga.b3.entityagent.desired;

import io.tinga.b3.protocol.GenericB3Message;

public interface DesiredGenericB3MessageProvider {
    public GenericB3Message load(String desiredRef);
}
