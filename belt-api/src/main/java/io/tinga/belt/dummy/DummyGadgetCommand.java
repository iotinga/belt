package io.tinga.belt.dummy;

import io.tinga.belt.Gadget;

public class DummyGadgetCommand implements Gadget.Command<DummyGadgetAction> {

    @Override
    public DummyGadgetAction action() {
        return DummyGadgetAction.DUMMY;
    }

}
