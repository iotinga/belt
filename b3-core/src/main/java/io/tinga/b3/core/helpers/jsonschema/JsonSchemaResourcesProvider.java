package io.tinga.b3.core.helpers.jsonschema;

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

import io.tinga.b3.protocol.topic.B3Topic;

public class JsonSchemaResourcesProvider implements JsonSchemaProvider {

    private final static Logger log = LoggerFactory.getLogger(JsonSchemaResourcesProvider.class);

    private static final String PATH_FORMAT = "%s/%s.json";

    @Inject
    private JsonSchemaConfig config;

    @Inject
    private ObjectMapper om;

    @Inject
    private JsonSchemaFactory factory;

    private final Map<String, JsonSchema> cache = new HashMap<>();

    @Override
    public JsonSchema getSchemaFor(B3Topic.Name topic) {
        String topicPath = topic.build();
        JsonSchema schema = this.config.isJsonSchemaCacheEnabled() ? this.cache.get(topicPath) : null;

        if (schema == null) {
            try {
                String resourcePath = String.format(PATH_FORMAT, config.getJsonSchemaBasePath(), topicPath);
                InputStream schemaInputStream = JsonSchemaResourcesProvider.class.getResourceAsStream(resourcePath);
                JsonNode jsonSchemaNode = this.om.readTree(schemaInputStream);
                schema = this.factory.getSchema(jsonSchemaNode);
                this.cache.put(topicPath, schema);
            } catch (IOException e) {
                log.error(String.format("%s: unable to load schema", topic));
            }
        }

        return schema;
    }
}
