package io.tinga.b3.entityagent;

import io.tinga.belt.input.GadgetCommandOption;

public enum EntityCommandOption implements GadgetCommandOption {

    MODE(GadgetCommandOption.ACTION_STANDARD_OPT, true, EntityCommandAction.class, EntityCommandAction.RESOURCES.name(),
            "Defines the action to perform, in particular if we have to serve MQTT requests or evaluate specific requests from Filesystem o Resources"),
    DESIRED(EntityCommand.DESIRED_REF_OPT, true, String.class, "/desired.json",
            "The path to the desired json input file. It must be a json representing the full MQTT message"),
    REPORTED(EntityCommand.REPORTED_REF_OPT, true, String.class, "/reported.json",
            "The path to the reported json input file. It must be a json object with topics as keys and full reported mqtt messages as values"),
    SCHEMAS(EntityCommand.SCHEMA_BASE_DIR_OPT, true, String.class, "/schema",
            "The schemas base directory. In this director the tool will search the schema using the topic as sub-path."),
    TOPIC(EntityCommand.TOPIC_OPT, true, String.class, "braid/entity/test/admin",
            "The topic to simulate as source of the desired."),

    HELP(GadgetCommandOption.HELP_STANDARD_OPT, false, null, null, "Shows this guide");

    private final String opt;
    private final boolean hasArg;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;

    private EntityCommandOption(String opt, boolean hasArg, Class<?> type, String defaultValue,
            String description) {
        this.opt = opt;
        this.hasArg = hasArg;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @Override
    public String opt() {
        return opt;
    }

    @Override
    public boolean hasArg() {
        return hasArg;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

}
