package io.tinga.b3.entityagent;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.Gadget;
import io.tinga.belt.input.GadgetCommandOption;

public class EntityAgentCommand implements Gadget.Command<EntityAgentCommandAction> {

    public static final String REPORTED_REF_OPT = "r";
    public static final String DESIRED_REF_OPT = "d";
    public static final String ROLE_OPT = "r";
    public static final String SCHEMA_BASE_DIR_OPT = "s";
    public static final String MODE_OPT = "m";

    @JsonProperty(GadgetCommandOption.ACTION_STANDARD_OPT)
    private EntityAgentCommandAction action;

    @JsonProperty(REPORTED_REF_OPT)
    private String reportedRef;

    @JsonProperty(DESIRED_REF_OPT)
    private String desiredRef;

    @JsonProperty(ROLE_OPT)
    private String role;

    @JsonProperty(SCHEMA_BASE_DIR_OPT)
    private String schemaBaseDir;

    public EntityAgentCommand() {}

    public EntityAgentCommand(String reportedRef, String desiredRef, String schemaBaseDir, String role,
            EntityAgentCommandAction action) {
        this.reportedRef = reportedRef;
        this.desiredRef = desiredRef;
        this.schemaBaseDir = schemaBaseDir;
        this.action = action;
        this.role = role;
    }

    public String role() {
        return role;
    }

    public String reportedRef() {
        return this.reportedRef;
    };

    public String desiredRef() {
        return this.desiredRef;
    };

    public String schemaBaseDir() {
        return this.schemaBaseDir;
    };

    public EntityAgentCommandAction action() {
        return this.action;
    }

}
