package com.chua.common.support.jsoup.helper;

import com.chua.common.support.jsoup.UncheckedException;
import com.chua.common.support.jsoup.internal.ConstrainableInputStream;
import com.chua.common.support.jsoup.internal.Normalizer;
import com.chua.common.support.jsoup.nodes.*;
import com.chua.common.support.jsoup.parser.Parser;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.utils.StringUtils;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Internal static utilities for handling data.
 *
 * @author Administrator
 */
@SuppressWarnings("CharsetObjectCanBeUsed")
public final class DataUtil {
    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"']*)");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    static final String DEFAULT_CHARSET_NAME = UTF_8.name();
    private static final int FIRST_READ_BUFFER_SIZE = 1024 * 5;
    static final int BUFFER_SIZE = 1024 * 32;
    private static final char[] MIME_BOUNDARY_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final int BOUNDARY_LENGTH = 32;

    private DataUtil() {
    }

    /**
     * Loads and parses a file to a Document, with the HtmlParser. Files that are compressed with gzip (and end in {@code .gz} or {@code .z})
     * are supported in addition to uncompressed files.
     *
     * @param file        file to load
     * @param charsetName (optional) character set of input; specify {@code null} to attempt to autodetect. A BOM in
     *                    the file will always override this setting.
     * @param baseUri     base URI of document, to resolve relative links against
     * @return Document
     * @throws IOException on IO error
     */
    public static Document load(File file, String charsetName, String baseUri) throws IOException {
        return load(file, charsetName, baseUri, Parser.htmlParser());
    }

    /**
     * Loads and parses a file to a Document. Files that are compressed with gzip (and end in {@code .gz} or {@code .z})
     * are supported in addition to uncompressed files.
     *
     * @param file        file to load
     * @param charsetName (optional) character set of input; specify {@code null} to attempt to autodetect. A BOM in
     *                    the file will always override this setting.
     * @param baseUri     base URI of document, to resolve relative links against
     * @param parser      alternate {@link Parser#xmlParser() parser} to use.
     * @return Document
     * @throws IOException on IO error
     * @since 1.14.2
     */
    public static Document load(File file, String charsetName, String baseUri, Parser parser) throws IOException {
        InputStream stream = new FileInputStream(file);
        String name = Normalizer.lowerCase(file.getName());
        if (name.endsWith(".gz") || name.endsWith(".z")) {
            boolean zipped;
            try {
                zipped = (stream.read() == 0x1f && stream.read() == 0x8b);
            } finally {
                stream.close();

            }
            stream = zipped ? new GZIPInputStream(new FileInputStream(file)) : new FileInputStream(file);
        }
        return parseInputStream(stream, charsetName, baseUri, parser);
    }

    /**
     * Parses a Document from an input steam.
     *
     * @param in          input stream to parse. The stream will be closed after reading.
     * @param charsetName character set of input (optional)
     * @param baseUri     base URI of document, to resolve relative links against
     * @return Document
     * @throws IOException on IO error
     */
    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return parseInputStream(in, charsetName, baseUri, Parser.htmlParser());
    }

    /**
     * Parses a Document from an input steam, using the provided Parser.
     *
     * @param in          input stream to parse. The stream will be closed after reading.
     * @param charsetName character set of input (optional)
     * @param baseUri     base URI of document, to resolve relative links against
     * @param parser      alternate {@link Parser#xmlParser() parser} to use.
     * @return Document
     * @throws IOException on IO error
     */
    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return parseInputStream(in, charsetName, baseUri, parser);
    }

    /**
     * Writes the input stream to the output stream. Doesn't close them.
     *
     * @param in  input stream to read from
     * @param out output stream to write to
     * @throws IOException on IO error
     */
    static void crossStreams(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    static Document parseInputStream(InputStream input, String charsetName, String baseUri, Parser parser) throws IOException {
        if (input == null) {
            return new Document(baseUri);
        }
        input = ConstrainableInputStream.wrap(input, BUFFER_SIZE, 0);

        Document doc = null;
        try {
            input.mark(BUFFER_SIZE);
            ByteBuffer firstBytes = readToByteBuffer(input, FIRST_READ_BUFFER_SIZE - 1);
            boolean fullyRead = (input.read() == -1);
            input.reset();
            BomCharset bomCharset = detectCharsetFromBom(firstBytes);
            if (bomCharset != null) {
                charsetName = bomCharset.charset;
            }

            if (charsetName == null) {
                try {
                    CharBuffer defaultDecoded = UTF_8.decode(firstBytes);
                    if (defaultDecoded.hasArray()) {
                        doc = parser.parseInput(new CharArrayReader(defaultDecoded.array(), defaultDecoded.arrayOffset(), defaultDecoded.limit()), baseUri);
                    } else {
                        doc = parser.parseInput(defaultDecoded.toString(), baseUri);
                    }
                } catch (UncheckedException e) {
                    throw e.ioException();
                }

                Elements metaElements = doc.select("meta[http-equiv=content-type], meta[charset]");
                String foundCharset = null;
                for (Element meta : metaElements) {
                    if (meta.hasAttr("http-equiv")) {
                        foundCharset = getCharsetFromContentType(meta.attr("content"));
                    }
                    if (foundCharset == null && meta.hasAttr("charset")) {
                        foundCharset = meta.attr("charset");
                    }
                    if (foundCharset != null) {
                        break;
                    }
                }

                if (foundCharset == null && doc.childNodeSize() > 0) {
                    Node first = doc.childNode(0);
                    XmlDeclaration decl = null;
                    if (first instanceof XmlDeclaration) {
                        decl = (XmlDeclaration) first;
                    } else if (first instanceof Comment) {
                        Comment comment = (Comment) first;
                        if (comment.isXmlDeclaration()) {
                            decl = comment.asXmlDeclaration();
                        }
                    }
                    if (decl != null) {
                        if ("xml".equalsIgnoreCase(decl.name())) {
                            foundCharset = decl.attr("encoding");
                        }
                    }
                }
                foundCharset = validateCharset(foundCharset);
                if (foundCharset != null && !foundCharset.equalsIgnoreCase(DEFAULT_CHARSET_NAME)) {
                    foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                    charsetName = foundCharset;
                    doc = null;
                } else if (!fullyRead) {
                    doc = null;
                }
            } else {
                Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            }
            if (doc == null) {
                if (charsetName == null) {
                    charsetName = DEFAULT_CHARSET_NAME;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(charsetName)), BUFFER_SIZE);
                try {
                    if (bomCharset != null && bomCharset.offset) {
                        long skipped = reader.skip(1);
                        Validate.isTrue(skipped == 1);
                    }
                    try {
                        doc = parser.parseInput(reader, baseUri);
                    } catch (UncheckedException e) {
                        throw e.ioException();
                    }
                    Charset charset = charsetName.equals(DEFAULT_CHARSET_NAME) ? UTF_8 : Charset.forName(charsetName);
                    doc.outputSettings().charset(charset);
                    if (!charset.canEncode()) {
                        doc.charset(UTF_8);
                    }
                } finally {
                    reader.close();
                }
            }
        } finally {
            input.close();
        }
        return doc;
    }

    /**
     * Read the input stream into a byte buffer. To deal with slow input streams, you may interrupt the thread this
     * method is executing on. The data read until being interrupted will be available.
     *
     * @param inStream the input stream to read from
     * @param maxSize  the maximum size in bytes to read from the stream. Set to 0 to be unlimited.
     * @return the filled byte buffer
     * @throws IOException if an exception occurs whilst reading from the input stream.
     */
    public static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        Validate.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
        final ConstrainableInputStream input = ConstrainableInputStream.wrap(inStream, BUFFER_SIZE, maxSize);
        return input.readToByteBuffer(maxSize);
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    /**
     * Parse out a charset from a content type header. If the charset is not supported, returns null (so the default
     * will kick in.)
     *
     * @param contentType e.g. "text/html; charset=EUC-JP"
     * @return "EUC-JP", or null if not found. Charset is trimmed and uppercased.
     */
    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            return validateCharset(charset);
        }
        return null;
    }

    private 
    static String validateCharset(String cs) {
        if (cs == null || cs.length() == 0) {
            return null;
        }
        cs = cs.trim().replaceAll("[\"']", "");
        try {
            if (Charset.isSupported(cs)) {
                return cs;
            }
            cs = cs.toUpperCase(Locale.ENGLISH);
            if (Charset.isSupported(cs)) {
                return cs;
            }
        } catch (IllegalCharsetNameException e) {
            // if our this charset matching fails.... we just take the default
        }
        return null;
    }

    /**
     * Creates a random string, suitable for use as a mime boundary
     */
    static String mimeBoundary() {
        final StringBuilder mime = StringUtils.borrowBuilder();
        final Random rand = new Random();
        for (int i = 0; i < BOUNDARY_LENGTH; i++) {
            mime.append(MIME_BOUNDARY_CHARS[rand.nextInt(MIME_BOUNDARY_CHARS.length)]);
        }
        return mime.toString();
    }

    private static BomCharset detectCharsetFromBom(final ByteBuffer byteData) {
        @SuppressWarnings("UnnecessaryLocalVariable") final Buffer buffer = byteData;
        buffer.mark();
        byte[] bom = new byte[4];
        if (byteData.remaining() >= bom.length) {
            byteData.get(bom);
            buffer.rewind();
        }
        boolean b = bom[0] == 0x00 && bom[1] == 0x00 && bom[2] == (byte) 0xFE && bom[3] == (byte) 0xFF || bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE && bom[2] == 0x00 && bom[3] == 0x00;
        boolean b1 = bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF || bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE;

        if (b) {
            return new BomCharset("UTF-32", false);
        } else if (b1) {
            return new BomCharset("UTF-16", false);
        } else if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
            return new BomCharset("UTF-8", true);
        }
        return null;
    }

    private static class BomCharset {
        private final String charset;
        private final boolean offset;

        public BomCharset(String charset, boolean offset) {
            this.charset = charset;
            this.offset = offset;
        }
    }
}
