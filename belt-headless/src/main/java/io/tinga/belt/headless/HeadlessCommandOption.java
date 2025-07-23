package io.tinga.belt.headless;

import java.util.List;

import io.tinga.belt.input.GadgetCommandOption;

public enum HeadlessCommandOption implements GadgetCommandOption {

    IGNORE("i", true, Boolean.class, "false",
            "If true and a module initialization fails it ignores the error continuing loading other modules"),
    ACTION("a", true, HeadlessAction.class, HeadlessAction.CONCURRENT.name(),
            String.format("Chooses the threading model for modules execution (%s, %s)",
                    HeadlessAction.CONCURRENT,
                    HeadlessAction.SEQUENTIAL)),
    NAME("n", true, String.class, "BRAID",
            "The name of the runtime. It will be used in logs end as section for .env root configuration parameters"),
    HELP("h", false, null, null, "Shows this guide"),
    MODULES(GadgetCommandOption.POSITIONAL_ARGS_OPT, false, List.class, null, "List of modules to start");

    private final String opt;
    private final boolean hasArg;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;

    private HeadlessCommandOption(String shortName, boolean hasArg, Class<?> type, String defaultValue,
            String description) {
        this.opt = shortName;
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
