package io.tinga.b3.entityagent;

import io.tinga.b3.agent.Agent;

public interface EntityAgentConfig extends Agent.LocalShadowingConfig {

    public static String ENTITY_REPORTED_TOPIC_FILTER = "REPORTED_TOPIC_FILTER";
    public static String ENTITY_MQTT_PAYLOAD_SHADOW_KEY = "MQTT_PAYLOAD_SHADOW_KEY";
    public static String ENTITY_DESIRED_TOPIC_FILTER = "DESIRED_TOPIC_FILTER";
    public static String ENTITY_RETAINED_STORE_ON_INIT_MILLIS = "RETAINED_STORE_WAIT_ON_INIT_MILLIS";
    public static String ENTITY_JSON_SCHEMA_BASE_PATH = "JSON_SCHEMA_BASE_PATH";
    public static String ENTITY_JSON_SCHEMA_CACHE_ENABLED = "JSON_SCHEMA_CACHE_ENABLED";
    public static String ENTITY_STDOUT_ENABLED = "STDOUT_ENABLED";
    public static String ENTITY_REPORTED_STORE_REF = "REPORTED_STORE_REF";

    public boolean isJsonSchemaCacheEnabled();
    public String getJsonSchemaBasePath();
    public String getReportedTopicFilter();
    public String getDesiredTopicFilter();

}
