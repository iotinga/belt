package io.tinga.belt.config;

import java.util.Map;
import java.util.Properties;

public interface PropertiesProvider {
    public static final String PROP_KEY_GLUE = "_";
    
    public Properties properties(String section);
    public void loadPropertiesSection(String section, Properties target);
    public Map<String, String> getSection(String section);
}
