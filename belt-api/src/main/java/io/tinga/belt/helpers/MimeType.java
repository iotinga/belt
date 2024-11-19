package io.tinga.belt.helpers;

import java.util.*;

public class MimeType {

    // Mappa per memorizzare le istanze dei MIME types
    private static final Map<String, MimeType> mimeTypeRegistry = new HashMap<>();
    private static final Map<MimeType, MimeType> synonyms = new HashMap<>();

    // Costanti per un elenco esteso di MIME types
    public static final MimeType APPLICATION_PDF = build("application", "pdf");
    public static final MimeType IMAGE_JPEG = build("image", "jpeg");
    public static final MimeType IMAGE_JPG = build("image", "jpg");
    public static final MimeType IMAGE_PNG = build("image", "png");
    public static final MimeType IMAGE_GIF = build("image", "gif");
    public static final MimeType IMAGE_BMP = build("image", "bmp");
    public static final MimeType IMAGE_WEBP = build("image", "webp");
    public static final MimeType IMAGE_TIFF = build("image", "tiff");
    public static final MimeType IMAGE_SVG_XML = build("image", "svg+xml");
    public static final MimeType IMAGE_X_ICON = build("image", "x-icon");
    
    public static final MimeType TEXT_HTML = build("text", "html");
    public static final MimeType TEXT_PLAIN = build("text", "plain");
    public static final MimeType TEXT_XML = build("text", "xml");
    public static final MimeType TEXT_CSS = build("text", "css");
    public static final MimeType TEXT_CSV = build("text", "csv");
    public static final MimeType TEXT_JAVASCRIPT = build("text", "javascript");
    public static final MimeType TEXT_MARKDOWN = build("text", "markdown");

    public static final MimeType APPLICATION_JSON = build("application", "json");
    public static final MimeType APPLICATION_CBOR = build("application", "cbor");
    public static final MimeType APPLICATION_XML = build("application", "xml");
    public static final MimeType APPLICATION_ZIP = build("application", "zip");
    public static final MimeType APPLICATION_OCTET_STREAM = build("application", "octet-stream");
    public static final MimeType APPLICATION_JAVASCRIPT = build("application", "javascript");
    public static final MimeType APPLICATION_X_JAVASCRIPT = build("application", "x-javascript");
    public static final MimeType APPLICATION_XHTML_XML = build("application", "xhtml+xml");
    public static final MimeType APPLICATION_X_TAR = build("application", "x-tar");
    public static final MimeType APPLICATION_X_RAR_COMPRESSED = build("application", "x-rar-compressed");
    public static final MimeType APPLICATION_X_SHOCKWAVE_FLASH = build("application", "x-shockwave-flash");
    public static final MimeType APPLICATION_X_WWW_FORM_URLENCODED = build("application", "x-www-form-urlencoded");
    public static final MimeType APPLICATION_RTF = build("application", "rtf");
    public static final MimeType APPLICATION_MSWORD = build("application", "msword");
    public static final MimeType APPLICATION_VND_MS_EXCEL = build("application", "vnd.ms-excel");
    public static final MimeType APPLICATION_VND_MS_POWERPOINT = build("application", "vnd.ms-powerpoint");
    public static final MimeType APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT = build("application", "vnd.oasis.opendocument.text");
    public static final MimeType APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET = build("application", "vnd.oasis.opendocument.spreadsheet");
    public static final MimeType APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION = build("application", "vnd.oasis.opendocument.presentation");
    public static final MimeType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT = build("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
    public static final MimeType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET = build("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final MimeType APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION = build("application", "vnd.openxmlformats-officedocument.presentationml.presentation");

    public static final MimeType AUDIO_MPEG = build("audio", "mpeg");
    public static final MimeType AUDIO_OGG = build("audio", "ogg");
    public static final MimeType AUDIO_WAV = build("audio", "wav");
    public static final MimeType AUDIO_WEBM = build("audio", "webm");
    public static final MimeType AUDIO_X_AAC = build("audio", "x-aac");
    public static final MimeType AUDIO_X_FLAC = build("audio", "x-flac");
    public static final MimeType AUDIO_MP4 = build("audio", "mp4");

    public static final MimeType VIDEO_MP4 = build("video", "mp4");
    public static final MimeType VIDEO_WEBM = build("video", "webm");
    public static final MimeType VIDEO_OGG = build("video", "ogg");
    public static final MimeType VIDEO_X_MATROSKA = build("video", "x-matroska");
    public static final MimeType VIDEO_X_FLV = build("video", "x-flv");
    public static final MimeType VIDEO_QUICKTIME = build("video", "quicktime");
    public static final MimeType VIDEO_X_MSVIDEO = build("video", "x-msvideo");

    static {
        synonyms.put(IMAGE_JPG, IMAGE_JPEG);
        synonyms.put(APPLICATION_X_JAVASCRIPT, APPLICATION_JAVASCRIPT);
        synonyms.put(TEXT_JAVASCRIPT, APPLICATION_JAVASCRIPT);
        synonyms.put(IMAGE_X_ICON, build("image", "vnd.microsoft.icon"));

        // Sinonimi per JSON
        synonyms.put(APPLICATION_JSON, build("application", "x-json"));
        synonyms.put(APPLICATION_JSON, build("text", "json"));

        // Sinonimi per XML
        synonyms.put(APPLICATION_XML, build("text", "xml"));
        synonyms.put(APPLICATION_XML, build("application", "x-xml"));
        synonyms.put(APPLICATION_XML, build("application", "xml-external-parsed-entity"));
        synonyms.put(TEXT_XML, APPLICATION_XML);
    }

    private final String type;
    private final String subtype;

    private MimeType(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public static MimeType build(String type, String subtype) {
        String key = type + "/" + subtype;
        return mimeTypeRegistry.computeIfAbsent(key, k -> new MimeType(type, subtype));
    }

    public static MimeType get(String mimeType) {
        MimeType type = mimeTypeRegistry.get(mimeType);
        return synonyms.getOrDefault(type, type);
    }

    public static MimeType bestMatch(MimeType mimeType, List<MimeType> candidates, MimeType defaultMimeType) {
        MimeType canonicalType = synonyms.getOrDefault(mimeType, mimeType);
        for (MimeType candidate : candidates) {
            MimeType canonicalCandidate = synonyms.getOrDefault(candidate, candidate);
            if (canonicalCandidate.equals(canonicalType)) {
                return candidate;
            }
            if (canonicalCandidate.type.equals(canonicalType.type) && canonicalCandidate.subtype.equals("*")) {
                return candidate;
            }
        }
        return defaultMimeType;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MimeType mimeType = (MimeType) o;
        return type.equals(mimeType.type) && subtype.equals(mimeType.subtype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subtype);
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }
}
