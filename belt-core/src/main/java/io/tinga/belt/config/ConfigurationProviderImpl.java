package io.tinga.belt.config;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class ConfigurationProviderImpl implements ConfigurationProvider {

    private final ObjectMapper om;
    private final PropertiesProvider provider;

    @Inject
    public ConfigurationProviderImpl(PropertiesProvider provider) {
        this.om = new ObjectMapper();
        this.provider = provider;
    }

    @Override
    public <IMPL extends INTERFACE, INTERFACE> INTERFACE config(String sectionName, Class<IMPL> clazz) {
        Map<String, String> section = this.provider.getSection(sectionName);
        return this.om.convertValue(section, clazz);
    }
    
}
