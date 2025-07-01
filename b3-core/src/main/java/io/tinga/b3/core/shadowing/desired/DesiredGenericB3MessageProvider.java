package io.tinga.b3.core.shadowing.desired;

import io.tinga.b3.protocol.GenericB3Message;

public interface DesiredGenericB3MessageProvider {
    public GenericB3Message load(String desiredRef);
}
