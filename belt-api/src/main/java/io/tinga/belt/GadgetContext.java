package io.tinga.belt;

import java.util.Properties;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.GadgetSink;

public record GadgetContext<C>(Properties properties, C command, GadgetCommandExecutor<C> executor, GadgetSink output) {}
