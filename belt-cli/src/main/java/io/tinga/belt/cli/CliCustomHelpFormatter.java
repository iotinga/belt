package io.tinga.belt.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CliCustomHelpFormatter extends HelpFormatter {
    public StringBuffer renderOptions(StringBuffer sb, Options options) {
        return this.renderOptions(sb, getWidth(), options, getLeftPadding(), getDescPadding());
    }
}
