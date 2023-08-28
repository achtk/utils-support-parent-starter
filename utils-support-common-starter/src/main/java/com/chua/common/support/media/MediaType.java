package com.chua.common.support.media;

import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.Ascii;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.ObjectUtils;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH;
import static com.chua.common.support.constant.CommonConstant.WILDCARD;
import static com.chua.common.support.utils.Preconditions.checkArgument;
import static com.chua.common.support.utils.Preconditions.checkNotNull;
import static com.chua.common.support.utils.StringUtils.normalizeToken;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * MediaType
 *
 * @author CH
 */
@EqualsAndHashCode
public class MediaType implements Serializable {
    private static final String CHARSET_ATTRIBUTE = "charset";
    private static final String APPLICATION_TYPE = "application";
    private static final String AUDIO_TYPE = "audio";
    private static final String IMAGE_TYPE = "image";
    private static final String TEXT_TYPE = "text";
    private static final String VIDEO_TYPE = "video";
    private static final String FONT_TYPE = "font";
    private static final MultiValueMap<String, String> UTF_8_CONSTANT_PARAMETERS = MapUtils.ofMultiMap(CHARSET_ATTRIBUTE, Ascii.toLowerCase(UTF_8.name()));
    private final String type;
    private final String subtype;
    private final MultiValueMap<String, String> parameters;
    private static final Map<MediaType, MediaType> KNOWN_TYPES = MapUtils.newHashMap();

    private Optional<Charset> parsedCharset;

    /**
     * 添加媒体
     * @param type 类型
     * @param subtype 子类型
     * @return this
     */
    private static MediaType createConstant(String type, String subtype) {
        return addKnownType(new MediaType(type, subtype, new MultiLinkedValueMap<>()));
    }
    private static MediaType createConstantUtf8(String type, String subtype) {
        MediaType mediaType = addKnownType(new MediaType(type, subtype, UTF_8_CONSTANT_PARAMETERS));
        mediaType.parsedCharset = Optional.of(UTF_8);
        return mediaType;
    }
    /**
     * 添加媒体
     * @param mediaType 媒体
     * @return this
     */
    private static MediaType addKnownType(MediaType mediaType) {
        KNOWN_TYPES.put(mediaType, mediaType);
        return mediaType;
    }
    /**
     * 初始化
     * @param type 类型
     * @param subtype 子类型
     * @return this
     */
    public static MediaType create(String type, String subtype) {
        MediaType mediaType = create(type, subtype, new MultiLinkedValueMap<>());
        mediaType.parsedCharset = Optional.empty();
        return mediaType;
    }

    private static MediaType create(
            String type, String subtype, MultiValueMap<String, String> parameters) {
        checkNotNull(type);
        checkNotNull(subtype);
        checkNotNull(parameters);
        String normalizedType = normalizeToken(type);
        String normalizedSubtype = normalizeToken(subtype);
        checkArgument(
                !WILDCARD.equals(normalizedType) || WILDCARD.equals(normalizedSubtype),
                "A wildcard type cannot be used with a non-wildcard subtype");
        MultiValueMap<String, String> tpl = new MultiLinkedValueMap<>(parameters);
        for (Map.Entry<String, String> entry : parameters.toSingleValueMap().entrySet()) {
            String attribute = normalizeToken(entry.getKey());
            tpl.add(attribute, normalizeParameterValue(attribute, entry.getValue()));
        }
        MediaType mediaType = new MediaType(normalizedType, normalizedSubtype, tpl);
        // Return one of the constants if the media type is a known type.
        return ObjectUtils.firstNonNull(KNOWN_TYPES.get(mediaType), mediaType);
    }

    private MediaType(String type, String subtype, MultiValueMap<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
    }

    /**
     * Returns the top-level media type. For example, {@code "text"} in {@code "text/plain"}.
     */
    public String type() {
        return type;
    }

    /**
     * Returns the media subtype. For example, {@code "plain"} in {@code "text/plain"}.
     */
    public String subtype() {
        return subtype;
    }

    private static String normalizeParameterValue(String attribute, String value) {
        checkNotNull(value);
        return CHARSET_ATTRIBUTE.equals(attribute) ? Ascii.toLowerCase(value) : value;
    }

    /**
     * 解析
     * @param input 输入
     * @return 结果
     */

    public static MediaType parse(String input) {
        checkNotNull(input);
        if(input.startsWith(SYMBOL_HASH)) {
            return ANY_TYPE;
        }

        String[] split = Splitter.onPattern("[\t]{1}").limit(2).useForNull("").split(input);
        String s1 = split[0];
        String[] split1 = s1.split("/");
        Map<String, String> integerMap = new LinkedHashMap<>();
        for (String s : Splitter.on("\\s+").splitToList(split[1])) {
            integerMap.put(s, s);
        }
        return MediaType.create(split1[0], split1[1], new MultiLinkedValueMap<>(integerMap));
    }


    public static final MediaType ANY_TYPE = createConstant(WILDCARD, WILDCARD);
    public static final MediaType ANY_TEXT_TYPE = createConstant(TEXT_TYPE, WILDCARD);
    public static final MediaType ANY_IMAGE_TYPE = createConstant(IMAGE_TYPE, WILDCARD);
    public static final MediaType ANY_AUDIO_TYPE = createConstant(AUDIO_TYPE, WILDCARD);
    public static final MediaType ANY_VIDEO_TYPE = createConstant(VIDEO_TYPE, WILDCARD);
    public static final MediaType ANY_APPLICATION_TYPE = createConstant(APPLICATION_TYPE, WILDCARD);

    /**
     * Wildcard matching any "font" top-level media type.
     *
     * @since 30.0
     */
    public static final MediaType ANY_FONT_TYPE = createConstant(FONT_TYPE, WILDCARD);

    public static final MediaType CACHE_MANIFEST_UTF_8 =
            createConstantUtf8(TEXT_TYPE, "cache-manifest");
    public static final MediaType CSS_UTF_8 = createConstantUtf8(TEXT_TYPE, "css");
    public static final MediaType CSV_UTF_8 = createConstantUtf8(TEXT_TYPE, "csv");
    public static final MediaType HTML_UTF_8 = createConstantUtf8(TEXT_TYPE, "html");
    public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8(TEXT_TYPE, "calendar");
    public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8(TEXT_TYPE, "plain");

    /**
     * <a href="http://www.rfc-editor.org/rfc/rfc4329.txt">RFC 4329</a> declares {@link
     * #JAVASCRIPT_UTF_8 application/javascript} to be the correct media type for JavaScript, but this
     * may be necessary in certain situations for compatibility.
     */
    public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8(TEXT_TYPE, "javascript");
    /**
     * <a href="http://www.iana.org/assignments/media-types/text/tab-separated-values">Tab separated
     * values</a>.
     *
     * @since 15.0
     */
    public static final MediaType TSV_UTF_8 = createConstantUtf8(TEXT_TYPE, "tab-separated-values");

    public static final MediaType VCARD_UTF_8 = createConstantUtf8(TEXT_TYPE, "vcard");

    /**
     * UTF-8 encoded <a href="https://en.wikipedia.org/wiki/Wireless_Markup_Language">Wireless Markup
     * Language</a>.
     *
     * @since 13.0
     */
    public static final MediaType WML_UTF_8 = createConstantUtf8(TEXT_TYPE, "vnd.wap.wml");

    /**
     * As described in <a href="http://www.ietf.org/rfc/rfc3023.txt">RFC 3023</a>, this constant
     * ({@code text/xml}) is used for XML documents that are "readable by casual users." {@link
     * #APPLICATION_XML_UTF_8} is provided for documents that are intended for applications.
     */
    public static final MediaType XML_UTF_8 = createConstantUtf8(TEXT_TYPE, "xml");

    /**
     * As described in <a href="https://w3c.github.io/webvtt/#iana-text-vtt">the VTT spec</a>, this is
     * used for Web Video Text Tracks (WebVTT) files, used with the HTML5 track element.
     *
     * @since 20.0
     */
    public static final MediaType VTT_UTF_8 = createConstantUtf8(TEXT_TYPE, "vtt");

    /* image types */
    /**
     * <a href="https://en.wikipedia.org/wiki/BMP_file_format">Bitmap file format</a> ({@code bmp}
     * files).
     *
     * @since 13.0
     */
    public static final MediaType BMP = createConstant(IMAGE_TYPE, "bmp");

    /**
     * The <a href="https://en.wikipedia.org/wiki/Camera_Image_File_Format">Canon Image File
     * Format</a> ({@code crw} files), a widely-used "raw image" format for cameras. It is found in
     * {@code /etc/mime.types}, e.g. in <a href=
     * "http://anonscm.debian.org/gitweb/?p=collab-maint/mime-support.git;a=blob;f=mime.types;hb=HEAD"
     * >Debian 3.48-1</a>.
     *
     * @since 15.0
     */
    public static final MediaType CRW = createConstant(IMAGE_TYPE, "x-canon-crw");

    public static final MediaType GIF = createConstant(IMAGE_TYPE, "gif");
    public static final MediaType ICO = createConstant(IMAGE_TYPE, "vnd.microsoft.icon");
    public static final MediaType JPEG = createConstant(IMAGE_TYPE, "jpeg");
    public static final MediaType PNG = createConstant(IMAGE_TYPE, "png");

    /**
     * The Photoshop File Format ({@code psd} files) as defined by <a
     * href="http://www.iana.org/assignments/media-types/image/vnd.adobe.photoshop">IANA</a>, and
     * found in {@code /etc/mime.types}, e.g. <a
     * href="http://svn.apache.org/repos/asf/httpd/httpd/branches/1.3.x/conf/mime.types"></a> of the
     * Apache <a href="http://httpd.apache.org/">HTTPD project</a>; for the specification, see <a
     * href="http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/PhotoshopFileFormats.htm">
     * Adobe Photoshop Document Format</a> and <a
     * href="http://en.wikipedia.org/wiki/Adobe_Photoshop#File_format">Wikipedia</a>; this is the
     * regular output/input of Photoshop (which can also export to various image formats; note that
     * files with extension "PSB" are in a distinct but related format).
     *
     * <p>This is a more recent replacement for the older, experimental type {@code x-photoshop}: <a
     * href="http://tools.ietf.org/html/rfc2046#section-6">RFC-2046.6</a>.
     *
     * @since 15.0
     */
    public static final MediaType PSD = createConstant(IMAGE_TYPE, "vnd.adobe.photoshop");

    public static final MediaType SVG_UTF_8 = createConstantUtf8(IMAGE_TYPE, "svg+xml");
    public static final MediaType TIFF = createConstant(IMAGE_TYPE, "tiff");

    /**
     * <a href="https://en.wikipedia.org/wiki/WebP">WebP image format</a>.
     *
     * @since 13.0
     */
    public static final MediaType WEBP = createConstant(IMAGE_TYPE, "webp");

    /**
     * <a href="https://www.iana.org/assignments/media-types/image/heif">HEIF image format</a>.
     *
     * @since 28.1
     */
    public static final MediaType HEIF = createConstant(IMAGE_TYPE, "heif");

    /**
     * <a href="https://tools.ietf.org/html/rfc3745">JP2K image format</a>.
     *
     * @since 28.1
     */
    public static final MediaType JP2K = createConstant(IMAGE_TYPE, "jp2");

    public static final MediaType MP4_AUDIO = createConstant(AUDIO_TYPE, "mp4");
    public static final MediaType MPEG_AUDIO = createConstant(AUDIO_TYPE, "mpeg");
    public static final MediaType OGG_AUDIO = createConstant(AUDIO_TYPE, "ogg");
    public static final MediaType WEBM_AUDIO = createConstant(AUDIO_TYPE, "webm");

    /**
     * L16 audio, as defined by <a href="https://tools.ietf.org/html/rfc2586">RFC 2586</a>.
     *
     * @since 24.1
     */
    public static final MediaType L16_AUDIO = createConstant(AUDIO_TYPE, "l16");

    /**
     * L24 audio, as defined by <a href="https://tools.ietf.org/html/rfc3190">RFC 3190</a>.
     *
     * @since 20.0
     */
    public static final MediaType L24_AUDIO = createConstant(AUDIO_TYPE, "l24");

    /**
     * Basic Audio, as defined by <a href="http://tools.ietf.org/html/rfc2046#section-4.3">RFC
     * 2046</a>.
     *
     * @since 20.0
     */
    public static final MediaType BASIC_AUDIO = createConstant(AUDIO_TYPE, "basic");

    /**
     * Advanced Audio Coding. For more information, see <a
     * href="https://en.wikipedia.org/wiki/Advanced_Audio_Coding">Advanced Audio Coding</a>.
     *
     * @since 20.0
     */
    public static final MediaType AAC_AUDIO = createConstant(AUDIO_TYPE, "aac");

    /**
     * Vorbis Audio, as defined by <a href="http://tools.ietf.org/html/rfc5215">RFC 5215</a>.
     *
     * @since 20.0
     */
    public static final MediaType VORBIS_AUDIO = createConstant(AUDIO_TYPE, "vorbis");

    /**
     * Windows Media Audio. For more information, see <a
     * href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx">file
     * name extensions for Windows Media metafiles</a>.
     *
     * @since 20.0
     */
    public static final MediaType WMA_AUDIO = createConstant(AUDIO_TYPE, "x-ms-wma");

    /**
     * Windows Media metafiles. For more information, see <a
     * href="https://msdn.microsoft.com/en-us/library/windows/desktop/dd562994(v=vs.85).aspx">file
     * name extensions for Windows Media metafiles</a>.
     *
     * @since 20.0
     */
    public static final MediaType WAX_AUDIO = createConstant(AUDIO_TYPE, "x-ms-wax");

    /**
     * Real Audio. For more information, see <a
     * href="http://service.real.com/help/faq/rp8/configrp8win.html">this link</a>.
     *
     * @since 20.0
     */
    public static final MediaType VND_REAL_AUDIO = createConstant(AUDIO_TYPE, "vnd.rn-realaudio");

    /**
     * WAVE format, as defined by <a href="https://tools.ietf.org/html/rfc2361">RFC 2361</a>.
     *
     * @since 20.0
     */
    public static final MediaType VND_WAVE_AUDIO = createConstant(AUDIO_TYPE, "vnd.wave");

    public static final MediaType MP4_VIDEO = createConstant(VIDEO_TYPE, "mp4");
    public static final MediaType MPEG_VIDEO = createConstant(VIDEO_TYPE, "mpeg");
    public static final MediaType OGG_VIDEO = createConstant(VIDEO_TYPE, "ogg");
    public static final MediaType QUICKTIME = createConstant(VIDEO_TYPE, "quicktime");
    public static final MediaType WEBM_VIDEO = createConstant(VIDEO_TYPE, "webm");
    public static final MediaType WMV = createConstant(VIDEO_TYPE, "x-ms-wmv");

    /**
     * Flash video. For more information, see <a href=
     * "http://help.adobe.com/en_US/ActionScript/3.0_ProgrammingAS3/WS5b3ccc516d4fbf351e63e3d118a9b90204-7d48.html"
     * >this link</a>.
     *
     * @since 20.0
     */
    public static final MediaType FLV_VIDEO = createConstant(VIDEO_TYPE, "x-flv");

    /**
     * The 3GP multimedia container format. For more information, see <a
     * href="ftp://www.3gpp.org/tsg_sa/TSG_SA/TSGS_23/Docs/PDF/SP-040065.pdf#page=10">3GPP TS
     * 26.244</a>.
     *
     * @since 20.0
     */
    public static final MediaType THREE_GPP_VIDEO = createConstant(VIDEO_TYPE, "3gpp");

    /**
     * The 3G2 multimedia container format. For more information, see <a
     * href="http://www.3gpp2.org/Public_html/specs/C.S0050-B_v1.0_070521.pdf#page=16">3GPP2
     * C.S0050-B</a>.
     *
     * @since 20.0
     */
    public static final MediaType THREE_GPP2_VIDEO = createConstant(VIDEO_TYPE, "3gpp2");

    /* application types */
    /**
     * As described in <a href="http://www.ietf.org/rfc/rfc3023.txt">RFC 3023</a>, this constant
     * ({@code application/xml}) is used for XML documents that are "unreadable by casual users."
     * {@link #XML_UTF_8} is provided for documents that may be read by users.
     *
     * @since 14.0
     */
    public static final MediaType APPLICATION_XML_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "xml");

    public static final MediaType ATOM_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "atom+xml");
    public static final MediaType BZIP2 = createConstant(APPLICATION_TYPE, "x-bzip2");

    /**
     * Files in the <a href="https://www.dartlang.org/articles/embedding-in-html/">dart</a>
     * programming language.
     *
     * @since 19.0
     */
    public static final MediaType DART_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "dart");

    /**
     * <a href="https://goo.gl/2QoMvg">Apple Passbook</a>.
     *
     * @since 19.0
     */
    public static final MediaType APPLE_PASSBOOK =
            createConstant(APPLICATION_TYPE, "vnd.apple.pkpass");

    /**
     * <a href="http://en.wikipedia.org/wiki/Embedded_OpenType">Embedded OpenType</a> fonts. This is
     * <a href="http://www.iana.org/assignments/media-types/application/vnd.ms-fontobject">registered
     * </a> with the IANA.
     *
     * @since 17.0
     */
    public static final MediaType EOT = createConstant(APPLICATION_TYPE, "vnd.ms-fontobject");

    /**
     * As described in the <a href="http://idpf.org/epub">International Digital Publishing Forum</a>
     * EPUB is the distribution and interchange format standard for digital publications and
     * documents. This media type is defined in the <a
     * href="http://www.idpf.org/epub/30/spec/epub30-ocf.html">EPUB Open Container Format</a>
     * specification.
     *
     * @since 15.0
     */
    public static final MediaType EPUB = createConstant(APPLICATION_TYPE, "epub+zip");

    public static final MediaType FORM_DATA =
            createConstant(APPLICATION_TYPE, "x-www-form-urlencoded");

    /**
     * As described in <a href="https://www.rsa.com/rsalabs/node.asp?id=2138">PKCS #12: Personal
     * Information Exchange Syntax Standard</a>, PKCS #12 defines an archive file format for storing
     * many cryptography objects as a single file.
     *
     * @since 15.0
     */
    public static final MediaType KEY_ARCHIVE = createConstant(APPLICATION_TYPE, "pkcs12");

    /**
     * This is a non-standard media type, but is commonly used in serving hosted binary files as it is
     * <a href="http://code.google.com/p/browsersec/wiki/Part2#Survey_of_content_sniffing_behaviors">
     * known not to trigger content sniffing in current browsers</a>. It <i>should not</i> be used in
     * other situations as it is not specified by any RFC and does not appear in the <a
     * href="http://www.iana.org/assignments/media-types">/IANA MIME Media Types</a> list. Consider
     * {@link #OCTET_STREAM} for binary data that is not being served to a browser.
     *
     * @since 14.0
     */
    public static final MediaType APPLICATION_BINARY = createConstant(APPLICATION_TYPE, "binary");

    /**
     * Media type for the <a href="https://tools.ietf.org/html/rfc7946">GeoJSON Format</a>, a
     * geospatial data interchange format based on JSON.
     *
     * @since 28.0
     */
    public static final MediaType GEO_JSON = createConstant(APPLICATION_TYPE, "geo+json");

    public static final MediaType GZIP = createConstant(APPLICATION_TYPE, "x-gzip");

    /**
     * <a href="https://tools.ietf.org/html/draft-kelly-json-hal-08#section-3">JSON Hypertext
     * Application Language (HAL) documents</a>.
     *
     * @since 26.0
     */
    public static final MediaType HAL_JSON = createConstant(APPLICATION_TYPE, "hal+json");

    /**
     * <a href="http://www.rfc-editor.org/rfc/rfc4329.txt">RFC 4329</a> declares this to be the
     * correct media type for JavaScript, but {@link #TEXT_JAVASCRIPT_UTF_8 text/javascript} may be
     * necessary in certain situations for compatibility.
     */
    public static final MediaType JAVASCRIPT_UTF_8 =
            createConstantUtf8(APPLICATION_TYPE, "javascript");

    /**
     * For <a href="https://tools.ietf.org/html/rfc7515">JWS or JWE objects using the Compact
     * Serialization</a>.
     *
     * @since 27.1
     */
    public static final MediaType JOSE = createConstant(APPLICATION_TYPE, "jose");

    /**
     * For <a href="https://tools.ietf.org/html/rfc7515">JWS or JWE objects using the JSON
     * Serialization</a>.
     *
     * @since 27.1
     */
    public static final MediaType JOSE_JSON = createConstant(APPLICATION_TYPE, "jose+json");

    public static final MediaType JSON_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "json");

    /**
     * The <a href="http://www.w3.org/TR/appmanifest/">Manifest for a web application</a>.
     *
     * @since 19.0
     */
    public static final MediaType MANIFEST_JSON_UTF_8 =
            createConstantUtf8(APPLICATION_TYPE, "manifest+json");

    /**
     * <a href="http://www.opengeospatial.org/standards/kml/">OGC KML (Keyhole Markup Language)</a>.
     */
    public static final MediaType KML = createConstant(APPLICATION_TYPE, "vnd.google-earth.kml+xml");

    /**
     * <a href="http://www.opengeospatial.org/standards/kml/">OGC KML (Keyhole Markup Language)</a>,
     * compressed using the ZIP format into KMZ archives.
     */
    public static final MediaType KMZ = createConstant(APPLICATION_TYPE, "vnd.google-earth.kmz");

    /**
     * The <a href="https://tools.ietf.org/html/rfc4155">mbox database format</a>.
     *
     * @since 13.0
     */
    public static final MediaType MBOX = createConstant(APPLICATION_TYPE, "mbox");

    /**
     * <a href="http://goo.gl/1pGBFm">Apple over-the-air mobile configuration profiles</a>.
     *
     * @since 18.0
     */
    public static final MediaType APPLE_MOBILE_CONFIG =
            createConstant(APPLICATION_TYPE, "x-apple-aspen-config");

    /** <a href="http://goo.gl/XDQ1h2">Microsoft Excel</a> spreadsheets. */
    public static final MediaType MICROSOFT_EXCEL = createConstant(APPLICATION_TYPE, "vnd.ms-excel");

    /**
     * <a href="http://goo.gl/XrTEqG">Microsoft Outlook</a> items.
     *
     * @since 27.1
     */
    public static final MediaType MICROSOFT_OUTLOOK =
            createConstant(APPLICATION_TYPE, "vnd.ms-outlook");

    /** <a href="http://goo.gl/XDQ1h2">Microsoft Powerpoint</a> presentations. */
    public static final MediaType MICROSOFT_POWERPOINT =
            createConstant(APPLICATION_TYPE, "vnd.ms-powerpoint");

    /** <a href="http://goo.gl/XDQ1h2">Microsoft Word</a> documents. */
    public static final MediaType MICROSOFT_WORD = createConstant(APPLICATION_TYPE, "msword");

    /**
     * Media type for <a
     * href="https://en.wikipedia.org/wiki/Dynamic_Adaptive_Streaming_over_HTTP">Dynamic Adaptive
     * Streaming over HTTP (DASH)</a>. This is <a
     * href="https://www.iana.org/assignments/media-types/application/dash+xml">registered</a> with
     * the IANA.
     *
     * @since 28.2
     */
    public static final MediaType MEDIA_PRESENTATION_DESCRIPTION =
            createConstant(APPLICATION_TYPE, "dash+xml");

    /**
     * WASM applications. For more information see <a href="https://webassembly.org/">the Web Assembly
     * overview</a>.
     *
     * @since 27.0
     */
    public static final MediaType WASM_APPLICATION = createConstant(APPLICATION_TYPE, "wasm");

    /**
     * NaCl applications. For more information see <a
     * href="https://developer.chrome.com/native-client/devguide/coding/application-structure">the
     * Developer Guide for Native Client Application Structure</a>.
     *
     * @since 20.0
     */
    public static final MediaType NACL_APPLICATION = createConstant(APPLICATION_TYPE, "x-nacl");

    /**
     * NaCl portable applications. For more information see <a
     * href="https://developer.chrome.com/native-client/devguide/coding/application-structure">the
     * Developer Guide for Native Client Application Structure</a>.
     *
     * @since 20.0
     */
    public static final MediaType NACL_PORTABLE_APPLICATION =
            createConstant(APPLICATION_TYPE, "x-pnacl");

    public static final MediaType OCTET_STREAM = createConstant(APPLICATION_TYPE, "octet-stream");

    public static final MediaType OGG_CONTAINER = createConstant(APPLICATION_TYPE, "ogg");
    public static final MediaType OOXML_DOCUMENT =
            createConstant(
                    APPLICATION_TYPE, "vnd.openxmlformats-officedocument.wordprocessingml.document");
    public static final MediaType OOXML_PRESENTATION =
            createConstant(
                    APPLICATION_TYPE, "vnd.openxmlformats-officedocument.presentationml.presentation");
    public static final MediaType OOXML_SHEET =
            createConstant(APPLICATION_TYPE, "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final MediaType OPENDOCUMENT_GRAPHICS =
            createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.graphics");
    public static final MediaType OPENDOCUMENT_PRESENTATION =
            createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.presentation");
    public static final MediaType OPENDOCUMENT_SPREADSHEET =
            createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.spreadsheet");
    public static final MediaType OPENDOCUMENT_TEXT =
            createConstant(APPLICATION_TYPE, "vnd.oasis.opendocument.text");

    /**
     * <a href="https://tools.ietf.org/id/draft-ellermann-opensearch-01.html">OpenSearch</a>
     * Description files are XML files that describe how a website can be used as a search engine by
     * consumers (e.g. web browsers).
     *
     * @since 28.2
     */
    public static final MediaType OPENSEARCH_DESCRIPTION_UTF_8 =
            createConstantUtf8(APPLICATION_TYPE, "opensearchdescription+xml");

    public static final MediaType PDF = createConstant(APPLICATION_TYPE, "pdf");
    public static final MediaType POSTSCRIPT = createConstant(APPLICATION_TYPE, "postscript");

    /**
     * <a href="http://tools.ietf.org/html/draft-rfernando-protocol-buffers-00">Protocol buffers</a>
     *
     * @since 15.0
     */
    public static final MediaType PROTOBUF = createConstant(APPLICATION_TYPE, "protobuf");

    /**
     * <a href="https://en.wikipedia.org/wiki/RDF/XML">RDF/XML</a> documents, which are XML
     * serializations of <a
     * href="https://en.wikipedia.org/wiki/Resource_Description_Framework">Resource Description
     * Framework</a> graphs.
     *
     * @since 14.0
     */
    public static final MediaType RDF_XML_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "rdf+xml");

    public static final MediaType RTF_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "rtf");

    /**
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares {@link #FONT_SFNT
     * font/sfnt} to be the correct media type for SFNT, but this may be necessary in certain
     * situations for compatibility.
     *
     * @since 17.0
     */
    public static final MediaType SFNT = createConstant(APPLICATION_TYPE, "font-sfnt");

    public static final MediaType SHOCKWAVE_FLASH =
            createConstant(APPLICATION_TYPE, "x-shockwave-flash");

    /**
     * {@code skp} files produced by the 3D Modeling software <a
     * href="https://www.sketchup.com/">SketchUp</a>
     *
     * @since 13.0
     */
    public static final MediaType SKETCHUP = createConstant(APPLICATION_TYPE, "vnd.sketchup.skp");

    /**
     * As described in <a href="http://www.ietf.org/rfc/rfc3902.txt">RFC 3902</a>, this constant
     * ({@code application/soap+xml}) is used to identify SOAP 1.2 message envelopes that have been
     * serialized with XML 1.0.
     *
     * <p>For SOAP 1.1 messages, see {@code XML_UTF_8} per <a
     * href="http://www.w3.org/TR/2000/NOTE-SOAP-20000508/">W3C Note on Simple Object Access Protocol
     * (SOAP) 1.1</a>
     *
     * @since 20.0
     */
    public static final MediaType SOAP_XML_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "soap+xml");

    public static final MediaType TAR = createConstant(APPLICATION_TYPE, "x-tar");

    /**
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares {@link #FONT_WOFF
     * font/woff} to be the correct media type for WOFF, but this may be necessary in certain
     * situations for compatibility.
     *
     * @since 17.0
     */
    public static final MediaType WOFF = createConstant(APPLICATION_TYPE, "font-woff");

    /**
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares {@link #FONT_WOFF2
     * font/woff2} to be the correct media type for WOFF2, but this may be necessary in certain
     * situations for compatibility.
     *
     * @since 20.0
     */
    public static final MediaType WOFF2 = createConstant(APPLICATION_TYPE, "font-woff2");

    public static final MediaType XHTML_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "xhtml+xml");

    /**
     * Extensible Resource Descriptors. This is not yet registered with the IANA, but it is specified
     * by OASIS in the <a href="http://docs.oasis-open.org/xri/xrd/v1.0/cd02/xrd-1.0-cd02.html">XRD
     * definition</a> and implemented in projects such as <a
     * href="http://code.google.com/p/webfinger/">WebFinger</a>.
     *
     * @since 14.0
     */
    public static final MediaType XRD_UTF_8 = createConstantUtf8(APPLICATION_TYPE, "xrd+xml");

    public static final MediaType ZIP = createConstant(APPLICATION_TYPE, "zip");

    /* font types */

    /**
     * A collection of font outlines as defined by <a href="https://tools.ietf.org/html/rfc8081">RFC
     * 8081</a>.
     *
     * @since 30.0
     */
    public static final MediaType FONT_COLLECTION = createConstant(FONT_TYPE, "collection");

    /**
     * <a href="https://en.wikipedia.org/wiki/OpenType">Open Type Font Format</a> (OTF) as defined by
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a>.
     *
     * @since 30.0
     */
    public static final MediaType FONT_OTF = createConstant(FONT_TYPE, "otf");

    /**
     * <a href="https://en.wikipedia.org/wiki/SFNT">Spline or Scalable Font Format</a> (SFNT). <a
     * href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares this to be the correct media
     * type for SFNT, but {@link #SFNT application/font-sfnt} may be necessary in certain situations
     * for compatibility.
     *
     * @since 30.0
     */
    public static final MediaType FONT_SFNT = createConstant(FONT_TYPE, "sfnt");

    /**
     * <a href="https://en.wikipedia.org/wiki/TrueType">True Type Font Format</a> (TTF) as defined by
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a>.
     *
     * @since 30.0
     */
    public static final MediaType FONT_TTF = createConstant(FONT_TYPE, "ttf");

    /**
     * <a href="http://en.wikipedia.org/wiki/Web_Open_Font_Format">Web Open Font Format</a> (WOFF). <a
     * href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares this to be the correct media
     * type for SFNT, but {@link #WOFF application/font-woff} may be necessary in certain situations
     * for compatibility.
     *
     * @since 30.0
     */
    public static final MediaType FONT_WOFF = createConstant(FONT_TYPE, "woff");

    /**
     * <a href="http://en.wikipedia.org/wiki/Web_Open_Font_Format">Web Open Font Format</a> (WOFF2).
     * <a href="https://tools.ietf.org/html/rfc8081">RFC 8081</a> declares this to be the correct
     * media type for SFNT, but {@link #WOFF2 application/font-woff2} may be necessary in certain
     * situations for compatibility.
     *
     * @since 30.0
     */
    public static final MediaType FONT_WOFF2 = createConstant(FONT_TYPE, "woff2");

    /**
     * 添加参数
     * @param name 参数名称
     * @param s 结果
     */
    public void addParameter(String name, String s) {
        parameters.put(name, s.split("\\s+"));
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }
}
