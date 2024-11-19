package io.tinga.belt.config.dotenv;

import java.util.Map;
import java.util.Properties;

import com.google.inject.Inject;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.tinga.belt.config.PropertiesProvider;

public class DotenvPropertiesProvider implements PropertiesProvider {

    protected final Dotenv env;

    @Inject
    public DotenvPropertiesProvider(Dotenv env) {
        this.env = env;
    }

    @Override
    public Properties properties(String section) {
        Properties retval = new Properties();
        this.loadPropertiesSection(section, retval);
        return retval;
    }

    @Override
    public void loadPropertiesSection(String section, Properties target) {
        String prefix = this.prefix(section);
        for (DotenvEntry entry : env.entries()) {
            if (entry.getKey().startsWith(prefix)) {
                target.setProperty(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
    }

    public String prefix(String section) {
        return String.format("%s%s", section, PropertiesProvider.PROP_KEY_GLUE);
    }

    @Override
    public Map<String, String> getSection(String section) {
        return new DotenvSectionsMapFacade(env, section);
    }
}
