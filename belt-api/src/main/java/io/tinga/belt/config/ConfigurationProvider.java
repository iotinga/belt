package io.tinga.belt.config;

public interface ConfigurationProvider {    
    public <IMPL extends INTERFACE, INTERFACE>  INTERFACE config(String section, Class<IMPL> clazz);
}
