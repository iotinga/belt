package io.tinga.b3.core.helpers.jsonschema;

import java.io.FileInputStream;
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

import io.tinga.b3.protocol.topic.B3TopicRoot;

public class JsonSchemaFromFileProvider implements JsonSchemaProvider {

    private final static Logger log = LoggerFactory.getLogger(JsonSchemaFromFileProvider.class);

    private static final String PATH_FORMAT = "%s/%s.json";

    @Inject
    private JsonSchemaConfig config;

    @Inject
    private ObjectMapper om;

    @Inject
    private JsonSchemaFactory factory;

    private final Map<String, JsonSchema> cache = new HashMap<>();

    @Override
    public JsonSchema getSchemaFor(B3TopicRoot.Name topic) {
        String topicPath = topic.build();
        JsonSchema schema = this.config.isJsonSchemaCacheEnabled() ? this.cache.get(topicPath) : null;
        if (schema == null) {
            try {
                String schemaFilePath = String.format(PATH_FORMAT, this.config.getJsonSchemaBasePath(), topicPath);
                InputStream schemaInputStream = new FileInputStream(schemaFilePath);
                JsonNode jsonSchemaNode = this.om.readTree(schemaInputStream);
                schema = this.factory.getSchema(jsonSchemaNode);
                if (this.config.isJsonSchemaCacheEnabled()) {
                    this.updateCache(topicPath, schema);
                }
            } catch (IOException e) {
                log.error(String.format("%s: unable to load schema", topic));
            }
        }

        return schema;
    }

    public void updateCache(String topic, JsonSchema schema) {
        this.cache.put(topic, schema);
    }

}
