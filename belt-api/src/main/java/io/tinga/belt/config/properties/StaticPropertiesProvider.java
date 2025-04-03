package io.tinga.belt.config.properties;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.helpers.PropertiesUtils;

public class StaticPropertiesProvider implements PropertiesProvider {

    public static final Logger log = LoggerFactory.getLogger(StaticPropertiesProvider.class);

    protected final Properties props;

    @Inject
    public StaticPropertiesProvider() {
        this.props = PropertiesUtils.loadProperties();
    }

    public StaticPropertiesProvider(String propertiesPath) {
        this.props = PropertiesUtils.loadProperties(propertiesPath);
    }

    public StaticPropertiesProvider(Properties properties) {
        this.props = properties;
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
        for (String name : this.props.stringPropertyNames()) {
            if (name.startsWith(prefix)) {
                target.setProperty(name.substring(prefix.length()), this.props.getProperty(name));
            }
        }
    }

    public String prefix(String section) {
        return String.format("%s%s", section, PropertiesProvider.PROP_KEY_GLUE);
    }

    @Override
    public Map<String, String> getSection(String section) {
        return new PropertiesSectionsMapFacade(props, section);
    }


}
