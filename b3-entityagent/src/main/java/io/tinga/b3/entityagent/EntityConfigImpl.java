package io.tinga.b3.entityagent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityConfigImpl implements EntityConfig {

    @JsonProperty(ENTITY_REPORTED_TOPIC_FILTER)
    private String reportedTopicFilter;

    @JsonProperty(ENTITY_MQTT_PAYLOAD_SHADOW_KEY)
    private String mqttPayloadShadowKey;

    @JsonProperty(ENTITY_DESIRED_TOPIC_FILTER)
    private String desiredTopicFilter;

    @JsonProperty(ENTITY_RETAINED_STORE_ON_INIT_MILLIS)
    private int retainedStoreWaitOnInitMillis;

    @JsonProperty(ENTITY_JSON_SCHEMA_BASE_PATH)
    private String jsonSchemaBasePath;

    @JsonProperty(ENTITY_JSON_SCHEMA_CACHE_ENABLED)
    private boolean jsonSchemaCacheEnabled;

    @JsonProperty(ENTITY_STDOUT_ENABLED)
    private boolean stdoutEnabled;

    @JsonProperty(ENTITY_REPORTED_STORE_REF)
    private String reportedStoreRef;

    @Override
    public String getReportedTopicFilter() {
        return reportedTopicFilter;
    }

    @Override
    public String getDesiredTopicFilter() {
        return desiredTopicFilter;
    }

    @Override
    public int getRetainedStoreWaitOnInitMillis() {
        return retainedStoreWaitOnInitMillis;
    }

    @Override
    public String getJsonSchemaBasePath() {
        return this.jsonSchemaBasePath;
    }

    @Override
    public boolean isJsonSchemaCacheEnabled() {
        return this.jsonSchemaCacheEnabled;
    }

    @Override
    public String getReportedStoreRef() {
        return this.reportedStoreRef;
    }

}
