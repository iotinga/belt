package io.tinga.b3.entityagent.jsonschema;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;

import io.tinga.b3.entityagent.EntityConfig;

public class JsonSchemaResourcesProvider implements JsonSchemaProvider {

    private final static Logger log = LoggerFactory.getLogger(JsonSchemaResourcesProvider.class);

    private static final String PATH_FORMAT = "%s/%s.json";

    @Inject
    private EntityConfig config;   
    
    @Inject
    private ObjectMapper om;
    
    @Inject
    private JsonSchemaFactory factory;

    private final Map<String, JsonSchema> cache = new HashMap<>();

    @Override
    public JsonSchema getSchemaFor(String topic) {
        JsonSchema schema = this.config.isJsonSchemaCacheEnabled() ? this.cache.get(topic) : null;

        if(schema == null) {
            try {
                String resourcePath = String.format(PATH_FORMAT, config.getJsonSchemaBasePath(), topic);
                InputStream schemaInputStream = JsonSchemaResourcesProvider.class.getResourceAsStream(resourcePath);
                JsonNode jsonSchemaNode = this.om.readTree(schemaInputStream);
                schema = this.factory.getSchema(jsonSchemaNode);
                this.cache.put(topic, schema);
            } catch (IOException e) {
                log.error(String.format("%s: unable to load schema", topic));
            }
        }

        return schema;
    }
}
