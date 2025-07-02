package io.tinga.b3.core.helpers.jsonschema;

import com.networknt.schema.JsonSchema;

public interface JsonSchemaProvider {
    public JsonSchema getSchemaFor(String topic);
}
