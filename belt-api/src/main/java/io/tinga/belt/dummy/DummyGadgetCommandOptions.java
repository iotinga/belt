package io.tinga.belt.dummy;

import io.tinga.belt.input.GadgetCommandOption;

public enum DummyGadgetCommandOptions implements GadgetCommandOption {
    HELP("h", false, null, null, "Shows this guide");

    private final String opt;
    private final boolean hasArg;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;

    DummyGadgetCommandOptions(String shortName, boolean hasArg, Class<?> type, String defaultValue,
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
