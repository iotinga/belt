package io.tinga.belt.headless;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.github.cdimascio.dotenv.Dotenv;
import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.cli.CliCommandFactory;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.config.ConfigurationProviderImpl;
import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.config.dotenv.DotenvPropertiesProvider;
import io.tinga.belt.config.properties.StaticPropertiesProvider;
import io.tinga.belt.helpers.PropertiesUtils;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.output.GadgetDisplayFactory;

public class CliCustomRootModule extends AbstractModule {
    private static final String CONFIGURATION_LOADER_DOTENV = "dotenv";
    private static final String CONFIGURATION_LOADER_PROPERTIES = "properties";

    private static final String CONFIGURATION_PATH_PROPERTY_KEY = "belt.config.path";
    private static final String CONFIGURATION_LOADER_PROPERTY_KEY = "belt.config.loader";

    @Override
    protected void configure() {
        bind(GadgetContextFactory.class).to(HeadelessGadgetContextFactory.class).in(Singleton.class);
        bind(GadgetCommandFactory.class).to(CliCommandFactory.class).in(Singleton.class);
        bind(ConfigurationProvider.class).to(ConfigurationProviderImpl.class).in(Singleton.class);
        bind(GadgetDisplayFactory.class).to(HeadlessDisplayFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public PropertiesProvider getPropertiesProvider() {
        String configType = System.getProperty(CONFIGURATION_LOADER_PROPERTY_KEY, CONFIGURATION_LOADER_DOTENV);

        return switch (configType) {
            case CONFIGURATION_LOADER_DOTENV -> new DotenvPropertiesProvider(buildDotenv());
            case CONFIGURATION_LOADER_PROPERTIES -> new StaticPropertiesProvider(System.getProperty(CONFIGURATION_PATH_PROPERTY_KEY, PropertiesUtils.DEFAULT_CONFIG_PROPERTIES_NAME));
            default -> throw new RuntimeException("invalid configuration type " + configType);
        };
    }

    @Provides
    @Singleton
    public Dotenv buildDotenv() {
        return Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
}
