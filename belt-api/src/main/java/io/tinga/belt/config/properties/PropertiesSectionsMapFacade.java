package io.tinga.belt.config.properties;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import io.tinga.belt.config.PropertiesProvider;

import java.util.HashSet;

public class PropertiesSectionsMapFacade implements Map<String, String> {

    private final Properties props;
    private final String prefix;

    public PropertiesSectionsMapFacade(Properties props, String prefix) {
        this.props = props;
        this.prefix = String.format("%s%s", prefix, PropertiesProvider.PROP_KEY_GLUE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Read only map");
    }

    @Override
    public String put(String arg0, String arg1) {
        throw new UnsupportedOperationException("Read only map");
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException("Read only map");
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException("Read only map");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Features are limited");
    }

    @Override
    public boolean containsKey(Object key) {
        String value = props.getProperty(_expand(key));
        return value != null;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> retval = new HashSet<>();
        for (Entry<Object, Object> entry : this.props.entrySet()) {
            if (entry.getKey().toString().startsWith(this.prefix))
                retval.add(new Entry<String, String>() {

                    @Override
                    public String getKey() {
                        return _normalize(entry.getKey());
                    }

                    @Override
                    public String getValue() {
                        return entry.getValue().toString();
                    }

                    @Override
                    public String setValue(String arg0) {
                        throw new UnsupportedOperationException("Read only map");
                    }

                });
        }
        return retval;
    }

    @Override
    public String get(Object key) {
        return this.props.getProperty(_expand(key));
    }

    @Override
    public boolean isEmpty() {
        return this.props.size() < 1;
    }

    @Override
    public Set<String> keySet() {
        Set<String> retval = new HashSet<>();
        for (String name : this.props.stringPropertyNames()) {
            if (name.startsWith(this.prefix))
                retval.add(_normalize(name));
        }
        return retval;
    }

    @Override
    public int size() {
        return this.props.size();
    }

    @Override
    public Collection<String> values() {
        Set<String> retval = new HashSet<>();
        for (String name : this.props.stringPropertyNames()) {
            if (name.startsWith(this.prefix))
                retval.add(this.props.getProperty(name));
        }
        return retval;
    }

    private String _expand(Object key) {
        return String.format("%s%s", this.prefix, key);
    }

    private String _normalize(Object key) {
        return String.format("%s", key).substring(this.prefix.length());
    }
}
