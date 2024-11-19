package io.tinga.belt.output;

import java.io.InputStream;

import io.tinga.belt.helpers.Encoding;
import io.tinga.belt.helpers.MimeType;

public interface GadgetSink {
    public void put(Object payload);

    public MimeType contenType();

    public Encoding encoding();

    public InputStream asStream();

    public void close();
}
