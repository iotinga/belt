package io.tinga.belt.headless;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.Gadget;
import io.tinga.belt.input.GadgetCommandOption;

public class HeadlessCommand implements Gadget.Command<HeadlessAction> {

    public static final String NAME_OPT = "n";
    public static final String IGNORE_OPT = "i";
    public static final String ACTION_OPT = "a";

    @JsonProperty(NAME_OPT)
    String name;

    @JsonProperty(ACTION_OPT)
    HeadlessAction action;

    @JsonProperty(IGNORE_OPT)
    Boolean ignore;

    @JsonProperty(GadgetCommandOption.POSITIONAL_ARGS_OPT)
    List<String> gadgets;

    public HeadlessCommand() {}

    public HeadlessCommand(String name, HeadlessAction threading, Boolean ignore, List<String> gadgets) {
        this.name = name;
        this.action = threading;
        this.ignore = ignore;
        this.gadgets = gadgets;
    }

    public String name() {
        return this.name;
    }

    public HeadlessAction action() {
        return this.action;
    }

    public Boolean ignore() {
        return this.ignore;
    }

    public List<String> gadgets() {
        return this.gadgets;
    }
}