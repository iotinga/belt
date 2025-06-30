package io.tinga.b3.entityagent.desired;

import io.tinga.b3.entityagent.operation.EntityMessage;

public class DesiredEntityMessageDummyProvider implements DesiredEntityMessageProvider {

    @Override
    public EntityMessage load(String desiredRef) {
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
