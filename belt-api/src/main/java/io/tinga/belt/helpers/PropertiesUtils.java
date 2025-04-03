package io.tinga.belt.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {

    public static final Logger log = LoggerFactory.getLogger(PropertiesUtils.class);
    public static final String DEFAULT_CONFIG_PROPERTIES_NAME = "belt.properties";
    public static String propertiesPath = null;
    
    public static void setPropertiesPath(String path) {
        propertiesPath = path;
    }

    public static String getPropertiesPath() {
        if (propertiesPath == null) {
            return DEFAULT_CONFIG_PROPERTIES_NAME;
        }

        return propertiesPath;
    }

    public static Properties loadProperties(String path) {
        Properties properties;
        if (path != null && (properties = loadPropertiesFromFile(path)) != null)
            return properties;

        if ((properties = loadPropertiesAsResource(DEFAULT_CONFIG_PROPERTIES_NAME)) != null)
            return properties;

        log.info("No {} properties found. Run with defaults.", DEFAULT_CONFIG_PROPERTIES_NAME);
        return new Properties();
    }

    public static Properties loadProperties() {
        return loadProperties(getPropertiesPath());
    }

    public static Properties loadPropertiesAsResource(String propertiesResourceName) {
        try (InputStream resourceStream = PropertiesUtils.class.getClassLoader()
                .getResourceAsStream(propertiesResourceName);) {
            Properties properties = new Properties();
            properties.load(resourceStream);
            return properties;
        } catch (NullPointerException e) {
            log.debug("Unable to load properties");
        } catch (IOException e) {
            log.debug(String.format("Unable to load config resource: %s", propertiesResourceName), e);
        }
        return null;
    }

    public static Properties loadPropertiesFromFile(String filePath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            log.debug(String.format("Unable to load config file: %s", filePath), e);
        }

        if (in != null) {
            try {
                Properties properties = new Properties();
                properties.load(in);
                return properties;
            } catch (IOException e) {
                log.warn("Invalid properties file format", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    log.debug("Input stream already closed");
                }
            }
        }
        return null;
    }
}
