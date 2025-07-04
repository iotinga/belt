package io.tinga.b3.prova;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Message;

public class Modulo extends AbstractModule {

    public static interface GenericInterface<M extends B3Message<?>> {

    }

    public static class TestGeneric<M extends B3Message<?>> implements GenericInterface<M> {
        private final Class<M> messageClass;

        @Inject
        public TestGeneric(Class<M> messageClass) {
            this.messageClass = messageClass;
        }

        public Class<M> getMessageClass() {
            return this.messageClass;
        }
    }

    @Override
    public void configure() {
        bind(Key.get(new TypeLiteral<Class<GenericB3Message>>(){})).toInstance(GenericB3Message.class);
        bind(Key.get(new TypeLiteral<GenericInterface<GenericB3Message>>(){})).to(Key.get(new TypeLiteral<TestGeneric<GenericB3Message>>(){}));
    }

    public static void main(String[] args) {
        Injector inj = Guice.createInjector(new Modulo());
        GenericInterface<GenericB3Message> instance = inj.getInstance(new Key<>(){});
        System.out.println(instance.getClass().getName());
    }
}
