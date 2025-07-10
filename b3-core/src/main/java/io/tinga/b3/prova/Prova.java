package io.tinga.b3.prova;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import it.netgrid.bauer.TopicFactory;

public class Prova {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        Injector i = Guice.createInjector(TopicFactory.getAsModule(properties), new ProvaModule());

        AgentProxy<GenericB3Message> proxy = i.getInstance(Key.get(new TypeLiteral<>(){}));
        proxy.getClass().getSimpleName();
    }
}
