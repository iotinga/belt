package io.tinga.belt.helpers;

import com.google.inject.Inject;

import it.netgrid.bauer.EventHandler;

public abstract class AEventHandler<M> implements EventHandler<M> {

    private final Class<M> eventClass;

    @Inject
    public AEventHandler(Class<M> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public Class<M> getEventClass() {
        return this.eventClass;
    }    
}
