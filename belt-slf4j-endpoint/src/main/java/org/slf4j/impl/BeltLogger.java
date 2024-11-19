package org.slf4j.impl;

import org.slf4j.Logger;
import org.slf4j.Marker;

import java.time.LocalDateTime;

import io.tinga.belt.output.GadgetLogLevel;
import io.tinga.belt.output.GadgetLogRecord;

public class BeltLogger implements Logger {

    private String name;
    private BeltLoggerFactory factory;

    public BeltLogger(String name, BeltLoggerFactory factory) {
        this.name = name;
        this.factory = factory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE, msg, new Object[] {}));
    }

    @Override
    public void trace(String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void trace(String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, arguments), arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                msg, new Object[] { t }));
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override
    public void trace(Marker marker, String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE, msg, new Object[] {}));
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                String.format(format, argArray), argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.TRACE,
                msg, new Object[] { t }));
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG, msg, new Object[] {}));
    }

    @Override
    public void debug(String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void debug(String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arguments), arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG, msg,
                        new Object[] { t }));
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    @Override
    public void debug(Marker marker, String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG, msg, new Object[] {}));
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG,
                String.format(format, arguments), arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.DEBUG, msg,
                        new Object[] { t }));
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO, msg, new Object[] {}));
    }

    @Override
    public void info(String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void info(String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arguments), arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                msg, new Object[] { t }));
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override
    public void info(Marker marker, String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO, msg, new Object[] {}));
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                String.format(format, arguments), arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.INFO,
                msg, new Object[] { t }));
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN, msg, new Object[] {}));
    }

    @Override
    public void warn(String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arguments), arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN, msg,
                        new Object[] { t }));
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override
    public void warn(Marker marker, String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN, msg, new Object[] {}));
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN,
                String.format(format, arguments), arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.WARN, msg,
                        new Object[] { t }));
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR, msg, new Object[] {}));
    }

    @Override
    public void error(String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void error(String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arguments), arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR, msg,
                        new Object[] { t }));
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    @Override
    public void error(Marker marker, String msg) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR, msg, new Object[] {}));
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arg), new Object[] { arg }));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arg1, arg2), new Object[] { arg1, arg2 }));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        this.factory.add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR,
                String.format(format, arguments), arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        this.factory
                .add(new GadgetLogRecord(this.name, LocalDateTime.now(), GadgetLogLevel.ERROR, msg,
                        new Object[] { t }));
    }

}
