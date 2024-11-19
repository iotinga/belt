package io.tinga.belt.testgadget;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.belt.input.GadgetCommandOption;

public class TestGadgetCommand {

    public static final String NAME_OPT = "n";
    public static final String IGNORE_OPT = "i";
    public static final String THREADING_OPT = "t";

    @JsonProperty(NAME_OPT)
    private String name;

    @JsonProperty(THREADING_OPT)
    private TestGadgetComposition threading;

    @JsonProperty(IGNORE_OPT)
    private Boolean ignore;

    @JsonProperty(GadgetCommandOption.POSITIONAL_ARGS_OPT)
    private List<String> plugins;

    public TestGadgetCommand() {
    }

    public TestGadgetCommand(String name, TestGadgetComposition threading, Boolean ignore,
            List<String> plugins) {
        this.name = name;
        this.threading = threading;
        this.ignore = ignore;
        this.plugins = plugins;
    }

    public String name() {
        return name;
    }

    public TestGadgetComposition threading() {
        return threading;
    }

    public Boolean ignore() {
        return ignore;
    }

    public List<String> plugins() {
        return plugins;
    }
}
