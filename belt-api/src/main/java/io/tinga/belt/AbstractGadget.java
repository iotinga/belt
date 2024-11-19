package io.tinga.belt;

import com.google.inject.AbstractModule;

import io.tinga.belt.input.GadgetCommandExecutor;

public abstract class AbstractGadget<E extends GadgetCommandExecutor<C>, C> extends AbstractModule implements Gadget<E, C> {}
