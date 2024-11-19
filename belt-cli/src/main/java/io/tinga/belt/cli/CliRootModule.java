package io.tinga.belt.cli;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.GadgetContextFactoryImpl;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.config.ConfigurationProviderImpl;
import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.config.properties.StaticPropertiesProvider;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.output.GadgetDisplayFactory;

public class CliRootModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(GadgetContextFactory.class).to(GadgetContextFactoryImpl.class).in(Singleton.class);
        bind(GadgetCommandFactory.class).to(CliCommandFactory.class).in(Singleton.class);
        bind(PropertiesProvider.class).to(StaticPropertiesProvider.class).in(Singleton.class);
        bind(ConfigurationProvider.class).to(ConfigurationProviderImpl.class).in(Singleton.class);
        bind(GadgetDisplayFactory.class).to(CliDisplayFactory.class).in(Singleton.class);
    }
}