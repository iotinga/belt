package io.tinga.b3.helpers;

import com.networknt.schema.JsonSchema;

import io.tinga.b3.protocol.B3Topic;

public interface JsonSchemaProvider {

    public interface Config {

        public String getJsonSchemaBasePath();

        public boolean isJsonSchemaCacheEnabled();
        
    }

    public JsonSchema getSchemaFor(B3Topic topic);
}
