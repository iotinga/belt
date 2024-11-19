package io.tinga.belt.headless;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.github.cdimascio.dotenv.Dotenv;
import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.GadgetContextFactoryImpl;
import io.tinga.belt.cli.CliCommandFactory;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.config.ConfigurationProviderImpl;
import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.config.dotenv.DotenvPropertiesProvider;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.output.GadgetDisplayFactory;

public class CliCustomRootModule extends AbstractModule {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    @Override
    protected void configure() {
        bind(GadgetContextFactory.class).to(GadgetContextFactoryImpl.class).in(Singleton.class);
        bind(GadgetCommandFactory.class).to(CliCommandFactory.class).in(Singleton.class);
        bind(PropertiesProvider.class).to(DotenvPropertiesProvider.class).in(Singleton.class);
        bind(ConfigurationProvider.class).to(ConfigurationProviderImpl.class).in(Singleton.class);
        bind(GadgetDisplayFactory.class).to(HeadlessDisplayFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public Dotenv buildDotenv() {
        return DOTENV;
    }

}
