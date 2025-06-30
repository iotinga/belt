package io.tinga.b3.entityagent;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.Gadget;
import io.tinga.belt.input.GadgetCommandOption;

public class EntityCommand implements Gadget.Command<EntityAgentAction> {

    public static final String NAME_OPT = "n";
    public static final String REPORTED_REF_OPT = "r";
    public static final String DESIRED_REF_OPT = "d";
    public static final String TOPIC_OPT = "t";
    public static final String SCHEMA_BASE_DIR_OPT = "s";
    public static final String MODE_OPT = "m";

    @JsonProperty(NAME_OPT)
    private String name;

    @JsonProperty(GadgetCommandOption.ACTION_STANDARD_OPT)
    private EntityAgentAction action;

    @JsonProperty(REPORTED_REF_OPT)
    private String reportedRef;

    @JsonProperty(DESIRED_REF_OPT)
    private String desiredRef;

    @JsonProperty(TOPIC_OPT)
    private String topic;

    @JsonProperty(SCHEMA_BASE_DIR_OPT)
    private String schemaBaseDir;

    @JsonProperty(MODE_OPT)
    private EntityInputMode mode;

    public EntityCommand() {}

    public EntityCommand(String name, String reportedRef, String desiredRef, String topic, String schemaBaseDir,
            EntityInputMode mode) {
        this.name = name;
        this.reportedRef = reportedRef;
        this.desiredRef = desiredRef;
        this.topic = topic;
        this.schemaBaseDir = schemaBaseDir;
        this.mode = mode;
    }

    public String name() {
        return this.name;
    };

    public String reportedRef() {
        return this.reportedRef;
    };

    public String desiredRef() {
        return this.desiredRef;
    };

    public String topic() {
        return this.topic;
    };

    public String schemaBaseDir() {
        return this.schemaBaseDir;
    };

    public EntityInputMode mode() {
        return this.mode;
    }

    @Override
    public EntityAgentAction action() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'action'");
    };

}
