package io.tinga.belt.headless;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.input.GadgetCommandOption;

public class HeadlessCommand {

    public static final String NAME_OPT = "n";
    public static final String IGNORE_OPT = "i";
    public static final String THREADING_OPT = "t";

    @JsonProperty(NAME_OPT)
    String name;

    @JsonProperty(THREADING_OPT)
    HeadlessGadgetComposition threading;

    @JsonProperty(IGNORE_OPT)
    Boolean ignore;

    @JsonProperty(GadgetCommandOption.POSITIONAL_ARGS_OPT)
    List<String> gadgets;

    public HeadlessCommand() {}

    public HeadlessCommand(String name, HeadlessGadgetComposition threading, Boolean ignore, List<String> gadgets) {
        this.name = name;
        this.threading = threading;
        this.ignore = ignore;
        this.gadgets = gadgets;
    }

    public String name() {
        return this.name;
    }

    public HeadlessGadgetComposition threading() {
        return this.threading;
    }

    public Boolean ignore() {
        return this.ignore;
    }

    public List<String> gadgets() {
        return this.gadgets;
    }
}