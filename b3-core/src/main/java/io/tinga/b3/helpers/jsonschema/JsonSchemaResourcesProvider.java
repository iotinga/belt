package io.tinga.b3.helpers.jsonschema;

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

import io.tinga.b3.helpers.JsonSchemaProvider;
import io.tinga.b3.protocol.B3Topic;

public class JsonSchemaResourcesProvider implements JsonSchemaProvider {

    private final static Logger log = LoggerFactory.getLogger(JsonSchemaResourcesProvider.class);

    private static final String PATH_FORMAT = "%s/%s.json";

    private final Config config;

    private final ObjectMapper om;

    private final JsonSchemaFactory factory;

    protected final Map<B3Topic, JsonSchema> cache = new HashMap<>();

    @Inject
    public JsonSchemaResourcesProvider(Config config, ObjectMapper om, JsonSchemaFactory factory) {
        this.config = config;
        this.om = om;
        this.factory = factory;
    }

    @Override
    public JsonSchema getSchemaFor(B3Topic topic) {
        JsonSchema schema = this.config.isJsonSchemaCacheEnabled() ? this.cache.get(topic) : null;

        if (schema == null) {
            try {
                JsonNode jsonSchemaNode = getJsonSchemaNode(topic);
                schema = this.factory.getSchema(jsonSchemaNode);
                if (this.config.isJsonSchemaCacheEnabled()) {
                    this.updateCache(topic, schema);
                }
            } catch (IOException e) {
                log.error(String.format("%s: unable to load schema", topic));
            }
        }

        return schema;
    }

    public JsonNode getJsonSchemaNode(B3Topic topic) throws IOException {
        InputStream inputStream = getSchemaInputStream(getSchemaPath(topic));
        return this.om.readTree(inputStream);
    }

    public String getSchemaPath(B3Topic topic) {
        return String.format(PATH_FORMAT, this.config.getJsonSchemaBasePath(), topic);
    }

    public InputStream getSchemaInputStream(String resourcePath) throws IOException {
        return JsonSchemaResourcesProvider.class.getResourceAsStream(resourcePath);
    }

    public void updateCache(B3Topic topic, JsonSchema schema) {
        this.cache.put(topic, schema);
    }
}
