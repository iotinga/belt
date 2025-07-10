package io.tinga.belt.input;

public interface GadgetCommandOption {
    public static final String HELP_STANDARD_OPT = "h";
    public static final String ACTION_STANDARD_OPT = "a";
    public static final String POSITIONAL_ARGS_OPT = "positional";
    public String name();
    public String opt();
    public boolean hasArg();
    public String description();
    public Class<?> type();
    public String defaultValue();
}
