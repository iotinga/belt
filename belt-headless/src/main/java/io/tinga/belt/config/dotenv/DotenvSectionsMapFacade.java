package io.tinga.belt.config.dotenv;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.tinga.belt.config.PropertiesProvider;

public class DotenvSectionsMapFacade implements Map<String, String> {

    private final String prefix;
    private final Dotenv ENV;

    public DotenvSectionsMapFacade(Dotenv env, String prefix) {
        this.ENV = env;
        this.prefix = String.format("%s%s", prefix, PropertiesProvider.PROP_KEY_GLUE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Read only map");
    }

    @Override
    public boolean containsKey(Object key) {
        String value = ENV.get(_expand(key));
        return value != null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Features are limited");
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> retval = new HashSet<>();
        for (DotenvEntry entry : ENV.entries()) {
            if (entry.getKey().startsWith(this.prefix))
                retval.add(new Entry<String, String>() {

                    @Override
                    public String getKey() {
                        return _normalize(entry.getKey());
                    }

                    @Override
                    public String getValue() {
                        return entry.getValue();
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
        return ENV.get(_expand(key));
    }

    @Override
    public boolean isEmpty() {
        return ENV.entries().size() < 1;
    }

    @Override
    public Set<String> keySet() {
        Set<String> retval = new HashSet<>();
        for (DotenvEntry entry : ENV.entries()) {
            if (entry.getKey().startsWith(this.prefix))
                retval.add(_normalize(entry.getKey()));
        }
        return retval;
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
    public int size() {
        return ENV.entries().size();
    }

    @Override
    public Collection<String> values() {
        Set<String> retval = new HashSet<>();

        for (DotenvEntry entry : ENV.entries()) {
            if (entry.getKey().startsWith(this.prefix))
                retval.add(entry.getValue());
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
