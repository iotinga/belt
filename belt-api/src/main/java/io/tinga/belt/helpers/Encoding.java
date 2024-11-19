package io.tinga.belt.helpers;

import java.util.*;
import java.nio.charset.Charset;

public class Encoding {

    // Mappa per memorizzare le istanze degli encoding types
    private static final Map<String, Encoding> encodingRegistry = new HashMap<>();
    private static final Map<Encoding, Encoding> synonyms = new HashMap<>();

    // Costanti per un elenco esteso di encoding types
    public static final Encoding RAW_BYTES = build("RAW-BYTES");
    public static final Encoding UTF_8 = build("UTF-8");
    public static final Encoding UTF_16 = build("UTF-16");
    public static final Encoding UTF_16BE = build("UTF-16BE");
    public static final Encoding UTF_16LE = build("UTF-16LE");
    public static final Encoding US_ASCII = build("US-ASCII");
    public static final Encoding ISO_8859_1 = build("ISO-8859-1");

    // Associazione di encoding types sinonimi
    static {
        synonyms.put(build("UTF8"), UTF_8);
        synonyms.put(build("UTF16"), UTF_16);
        synonyms.put(build("ASCII"), US_ASCII);
        synonyms.put(build("ISO8859-1"), ISO_8859_1);
        synonyms.put(build("ISO-LATIN-1"), ISO_8859_1);
    }

    private final String name;
    private Charset charset;

    private Encoding(String name) {
        this.name = name;
        try {
            this.charset = Charset.forName(name);
        } catch(Exception e) {}
    }

    public static Encoding build(String name) {
        return encodingRegistry.computeIfAbsent(name.toUpperCase(), k -> new Encoding(name));
    }

    public static Encoding get(String encodingName) {
        Encoding type = encodingRegistry.get(encodingName.toUpperCase());
        return synonyms.getOrDefault(type, type);
    }

    public static Encoding bestMatch(Encoding encoding, List<Encoding> candidates, Encoding defaultEncoding) {
        Encoding canonicalEncoding = synonyms.getOrDefault(encoding, encoding);
        for (Encoding candidate : candidates) {
            Encoding canonicalCandidate = synonyms.getOrDefault(candidate, candidate);
            if (canonicalCandidate.equals(canonicalEncoding)) {
                return candidate;
            }
        }
        return defaultEncoding;
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encoding that = (Encoding) o;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toUpperCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
