package io.tinga.b3.core.helpers.jsonschema;

import com.networknt.schema.JsonSchema;

import io.tinga.b3.protocol.topic.B3TopicRoot;

public interface JsonSchemaProvider {
    public JsonSchema getSchemaFor(B3TopicRoot.Name topic);
}
