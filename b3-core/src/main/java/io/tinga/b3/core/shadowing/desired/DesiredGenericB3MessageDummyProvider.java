package io.tinga.b3.core.shadowing.desired;

import io.tinga.b3.protocol.GenericB3Message;

public class DesiredGenericB3MessageDummyProvider implements DesiredGenericB3MessageProvider {

    @Override
    public GenericB3Message load(String desiredRef) {
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
