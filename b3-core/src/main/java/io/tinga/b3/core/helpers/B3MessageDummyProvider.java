package io.tinga.b3.core.helpers;

import io.tinga.b3.protocol.B3Message;

public class B3MessageDummyProvider<M extends B3Message<?>> implements B3MessageProvider<M> {

    @Override
    public M load(String desiredRef) {
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
