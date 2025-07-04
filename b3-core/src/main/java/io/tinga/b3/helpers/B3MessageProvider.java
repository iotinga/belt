package io.tinga.b3.helpers;

import io.tinga.b3.protocol.B3Message;

public interface B3MessageProvider<M extends B3Message<?>> {
    public M load(String desiredRef);
}
