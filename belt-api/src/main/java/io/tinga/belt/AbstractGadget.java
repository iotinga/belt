package io.tinga.belt;

import com.google.inject.AbstractModule;

public abstract class AbstractGadget<C extends Gadget.Command<?>> extends AbstractModule implements Gadget<C> {}
