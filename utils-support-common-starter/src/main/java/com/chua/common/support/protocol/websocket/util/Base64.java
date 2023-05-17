/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package com.chua.common.support.protocol.websocket.util;

/**
 * <p>Encodes and decodes to and from Base64 notation.</p>
 * <p>Homepage: <a href="http://iharder.net/base64">http://iharder.net/base64</a>.</p>
 *
 * <p>Example:</p>
 *
 * <code>String encoded = Base64.encode( myByteArray );</code>
 * <br>
 * <code>byte[] myByteArray = Base64.decode( encoded );</code>
 *
 * <p>The <tt>options</tt> parameter, which appears in a few places, is used to pass
 * several pieces of information to the encoder. In the "higher level" methods such as encodeBytes(
 * bytes, options ) the options parameter can be used to indicate such things as first gzipping the
 * bytes before encoding them, not inserting linefeeds, and encoding using the URL-safe and Ordered
 * dialects.</p>
 *
 * <p>Note, according to <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>,
 * Section 2.1, implementations should not add line feeds unless explicitly told to do so. I've got
 * Base64 set to this behavior now, although earlier versions broke lines by default.</p>
 *
 * <p>The constants defined in Base64 can be OR-ed together to combine options, so you
 * might make a call like this:</p>
 *
 * <code>String encoded = Base64.encodeBytes( mybytes, Base64.GZIP | Base64.DO_BREAK_LINES
 * );</code>
 * <p>to compress the data before encoding it and then making the output have newline
 * characters.</p>
 * <p>Also...</p>
 * <code>String encoded = Base64.encodeBytes( crazyString.getBytes() );</code>
 *
 *
 *
 * <p>
 * Change Log:
 * </p>
 * <ul>
 *  <li>v2.3.7 - Fixed subtle bug when base 64 input stream contained the
 *   value 01111111, which is an invalid base 64 character but should not
 *   throw an ArrayIndexOutOfBoundsException either. Led to discovery of
 *   mishandling (or potential for better handling) of other bad input
 *   characters. You should now get an IOException if you try decoding
 *   something that has bad characters in it.</li>
 *  <li>v2.3.6 - Fixed bug when breaking lines and the final byte of the encoded
 *   string ended in the last column; the buffer was not properly shrunk and
 *   contained an extra (null) byte that made it into the string.</li>
 *  <li>v2.3.4 - Fixed bug when working with gzipped streams whereby flushing
 *   the Base64.OutputStream closed the Base64 encoding (by padding with equals
 *   signs) too soon. Also added an option to suppress the automatic decoding
 *   of gzipped streams. Also added experimental support for specifying a
 *   class loader when using the method.</li>
 *  <li>v2.3.3 - Changed default char encoding to US-ASCII which reduces the internal Java
 *   footprint with its CharEncoders and so forth. Fixed some javadocs that were
 *   inconsistent. Removed imports and specified things like java.io.IOException
 *   explicitly inline.</li>
 *  <li>v2.3.2 - Reduced memory footprint! Finally refined the "guessing" of how big the
 *   final encoded data will be so that the code doesn't have to create two output
 *   arrays: an oversized initial one and then a final, exact-sized one. Big win
 *   when using the family of methods (and not
 *   using the gzip options which uses a different mechanism with streams and stuff).</li>
 *  <li>v2.3.1 - Added {@link #encodeBytesToBytes(byte[], int, int, int)} and some
 *   similar helper methods to be more efficient with memory by not returning a
 *   String but just a byte array.</li>
 *  <li>v2.3 - <strong>This is not a drop-in replacement!</strong> This is two years of comments
 *   and bug fixes queued up and finally executed. Thanks to everyone who sent
 *   me stuff, and I'm sorry I wasn't able to distribute your fixes to everyone else.
 *   Much bad coding was cleaned up including throwing exceptions where necessary
 *   instead of returning null values or something similar. Here are some changes
 *   that may affect you:
 *   <ul>
 *    <li><em>Does not break lines, by default.</em> This is to keep in compliance with
 *      <a href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>.</li>
 *    <li><em>Throws exceptions instead of returning null values.</em> Because some operations
 *      (especially those that may permit the GZIP option) use IO streams, there
 *      is a possibility of an java.io.IOException being thrown. After some discussion and
 *      thought, I've changed the behavior of the methods to throw java.io.IOExceptions
 *      rather than return null if ever there's an error. I think this is more
 *      appropriate, though it will require some changes to your code. Sorry,
 *      it should have been done this way to begin with.</li>
 *    <li><em>Removed all references to System.out, System.err, and the like.</em>
 *      Shame on me. All I can say is sorry they were ever there.</li>
 *    <li><em>Throws IllegalArgumentExceptions</em> as needed
 *      such as when passed arrays are null or offsets are invalid.</li>
 *    <li>Cleaned up as much javadoc as I could to avoid any javadoc warnings.
 *      This was especially annoying before for people who were thorough in their
 *      own projects and then had gobs of javadoc warnings on this file.</li>
 *   </ul>
 *  <li>v2.2.1 - Fixed bug using URL_SAFE and ORDERED encodings. Fixed bug
 *   when using very small files (~&lt; 40 bytes).</li>
 *  <li>v2.2 - Added some helper methods for encoding/decoding directly from
 *   one file to the next. Also added a main() method to support command line
 *   encoding/decoding from one file to the next. Also added these Base64 dialects:
 *   <ol>
 *   <li>The default is RFC3548 format.</li>
 *   <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.URLSAFE_FORMAT) generates
 *   URL and file name friendly format as described in Section 4 of RFC3548.
 *   http://www.faqs.org/rfcs/rfc3548.html</li>
 *   <li>Calling Base64.setFormat(Base64.BASE64_FORMAT.ORDERED_FORMAT) generates
 *   URL and file name friendly format that preserves lexical ordering as described
 *   in http://www.faqs.org/qa/rfcc-1940.html</li>
 *   </ol>
 *   Special thanks to Jim Kellerman at <a href="http://www.powerset.com/">http://www.powerset.com/</a>
 *   for contributing the new Base64 dialects.
 *  </li>
 *
 *  <li>v2.1 - Cleaned up javadoc comments and unused variables and methods. Added
 *   some convenience methods for reading and writing to and from files.</li>
 *  <li>v2.0.2 - Now specifies UTF-8 encoding in places where the code fails on systems
 *   with other encodings (like EBCDIC).</li>
 *  <li>v2.0.1 - Fixed an error when decoding a single byte, that is, when the
 *   encoded data was a single byte.</li>
 *  <li>v2.0 - I got rid of methods that used booleans to set options.
 *   Now everything is more consolidated and cleaner. The code now detects
 *   when data that's being decoded is gzip-compressed and will decompress it
 *   automatically. Generally things are cleaner. You'll probably have to
 *   change some method calls that you were making to support the new
 *   options format (<tt>int</tt>s that you "OR" together).</li>
 *  <li>v1.5.1 - Fixed bug when decompressing and decoding to a
 *   byte[] using <tt>decode( String s, boolean gzipCompressed )</tt>.
 *   Added the ability to "suspend" encoding in the Output Stream so
 *   you can turn on and off the encoding if you need to embed base64
 *   data in an otherwise "normal" stream (like an XML file).</li>
 *  <li>v1.5 - Output stream pases on flush() command but doesn't do anything itself.
 *      This helps when using GZIP streams.
 *      Added the ability to GZip-compress objects before encoding them.</li>
 *  <li>v1.4 - Added helper methods to read/write files.</li>
 *  <li>v1.3.6 - Fixed OutputStream.flush() so that 'position' is reset.</li>
 *  <li>v1.3.5 - Added flag to turn on and off line breaks. Fixed bug in input stream
 *      where last buffer being read, if not completely full, was not returned.</li>
 *  <li>v1.3.4 - Fixed when "improperly padded stream" error was thrown at the wrong time.</li>
 *  <li>v1.3.3 - Fixed I/O streams which were totally messed up.</li>
 * </ul>
 *
 * <p>
 * I am placing this code in the Public Domain. Do with it as you will.
 * This software comes with no guarantees or warranties but with
 * plenty of well-wishing instead!
 * Please visit <a href="http://iharder.net/base64">http: * periodically to check for updates or to contribute improvements.
 * </p>
 *
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.3.7
 */
public class Base64 {

    /* ********  P U B L I C   F I E L D S  ******** */


    /**
     * No options specified. Value is zero.
     */
    public static final int NO_OPTIONS = 0;

    /**
     * Specify encoding in first bit. Value is one.
     */
    public static final int ENCODE = 1;

    /**
     * Specify that data should be gzip-compressed in second bit. Value is two.
     */
    public static final int GZIP = 2;

    /**
     * Do break lines when encoding. Value is 8.
     */
    public static final int DO_BREAK_LINES = 8;

    /**
     * Encode using Base64-like encoding that is URL- and Filename-safe as described in Section 4 of
     * RFC3548:
     * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
     * It is important to note that data encoded this way is <em>not</em> officially valid Base64, or
     * at the very least should not be called Base64 without also specifying that is was encoded using
     * the URL- and Filename-safe dialect.
     */
    public static final int URL_SAFE = 16;


    /**
     * Encode using the special "ordered" dialect of Base64 described here:
     * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
     */
    public static final int ORDERED = 32;


    /* ********  P R I V A T E   F I E L D S  ******** */


    /**
     * Maximum line length (76) of Base64 output.
     */
    private static final int MAX_LINE_LENGTH = 76;


    /**
     * The equals sign (=) as a byte.
     */
    private static final byte EQUALS_SIGN = (byte) '=';


    /**
     * The new line character (\n) as a byte.
     */
    private static final byte NEW_LINE = (byte) '\n';


    /**
     * Preferred encoding.
     */
    private static final String PREFERRED_ENCODING = "US-ASCII";


    private static final byte WHITE_SPACE_ENC = -5;

    /* ********  S T A N D A R D   B A S E 6 4   A L P H A B E T  ******** */

    /**
     * The 64 valid Base64 values.
     */
    private static final byte[] STANDARD_ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };


    /**
     * Translates a Base64 value to either its 6-bit reconstruction value or a negative number
     * indicating some other meaning.
     **/
    private static final byte[] STANDARD_DECODABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};


    /* ********  U R L   S A F E   B A S E 6 4   A L P H A B E T  ******** */

    /**
     * Used in the URL- and Filename-safe dialect described in Section 4 of RFC3548:
     * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
     * Notice that the last two bytes become "hyphen" and "underscore" instead of "plus" and "slash."
     */
    private static final byte[] URL_SAFE_ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_'
    };

    /**
     * Used in decoding URL- and Filename-safe dialects of Base64.
     */
    private static final byte[] URL_SAFE_DECODABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};



    /* ********  O R D E R E D   B A S E 6 4   A L P H A B E T  ******** */

    /**
     * I don't get the point of this technique, but someone requested it, and it is described here:
     * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
     */
    private static final byte[] ORDERED_ALPHABET = {
            (byte) '-',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) '_',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z'
    };

    /**
     * Used in decoding the "ordered" dialect of Base64.
     */
    private static final byte[] ORDERED_DECODABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};


    /* ********  D E T E R M I N E   W H I C H   A L H A B E T  ******** */


    /**
     * Returns one of the _SOMETHING_ALPHABET byte arrays depending on the options specified. It's
     * possible, though silly, to specify ORDERED <b>and</b> URLSAFE in which case one of them will be
     * picked, though there is no guarantee as to which one will be picked.
     */
    private static final byte[] getAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return URL_SAFE_ALPHABET;
        } else if ((options & ORDERED) == ORDERED) {
            return ORDERED_ALPHABET;
        } else {
            return STANDARD_ALPHABET;
        }
    }

    /**
     * Returns one of the _SOMETHING_DECODABET byte arrays depending on the options specified. It's
     * possible, though silly, to specify ORDERED and URL_SAFE in which case one of them will be
     * picked, though there is no guarantee as to which one will be picked.
     */
    private static final byte[] getDecodabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return URL_SAFE_DECODABET;
        } else if ((options & ORDERED) == ORDERED) {
            return ORDERED_DECODABET;
        } else {
            return STANDARD_DECODABET;
        }
    }

    /**
     * Defeats instantiation.
     */
    private Base64() {
    }




    /* ********  E N C O D I N G   M E T H O D S  ******** */


    /**
     * Encodes up to the first three bytes of array <var>threeBytes</var> and returns a four-byte
     * array in Base64 notation. The actual number of significant bytes in your array is given by
     * <var>numSigBytes</var>. The array <var>threeBytes</var> needs only be as big as
     * <var>numSigBytes</var>.
     * Code can reuse a byte array by passing a four-byte array as <var>b4</var>.
     *
     * @param b4          A reusable byte array to reduce array instantiation
     * @param threeBytes  the array to convert
     * @param numSigBytes the number of significant bytes in your array
     * @return four byte array in Base64 notation.
     * @since 1.5.1
     */
    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }

    /**
     * <p>Encodes up to three bytes of the array <var>source</var>
     * and writes the resulting four Base64 bytes to <var>destination</var>. The source and
     * destination arrays can be manipulated anywhere along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays are large enough to accommodate
     * <var>srcOffset</var> + 3 for the <var>source</var> array or <var>destOffset</var> + 4 for the
     * <var>destination</var> array. The actual number of significant bytes in your array is given by
     * <var>numSigBytes</var>.</p>
     * <p>This is the lowest level of the encoding methods with
     * all possible parameters.</p>
     *
     * @param source      the array to convert
     * @param srcOffset   the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset  the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    private static byte[] encode3to4(
            byte[] source, int srcOffset, int numSigBytes,
            byte[] destination, int destOffset, int options) {

        final byte[] alphabet = getAlphabet(options);


        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
                | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
                | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = alphabet[(inBuff >>> 18)];
                destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = alphabet[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = alphabet[(inBuff) & 0x3f];
                return destination;

            case 2:
                destination[destOffset] = alphabet[(inBuff >>> 18)];
                destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = alphabet[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            case 1:
                destination[destOffset] = alphabet[(inBuff >>> 18)];
                destination[destOffset + 1] = alphabet[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            default:
                return destination;
        }
    }

    /**
     * Encodes a byte array into Base64 notation. Does not GZip-compress data.
     *
     * @param source The data to convert
     * @return The data in Base64-encoded form
     * @throws IllegalArgumentException if source array is null
     * @since 1.4
     */
    public static String encodeBytes(byte[] source) {
        // we're not going to have an java.io.IOException thrown, so
        String encoded = null;
        try {
            encoded = encodeBytes(source, 0, source.length, NO_OPTIONS);
        } catch (java.io.IOException ex) {
            assert false : ex.getMessage();
        }
        assert encoded != null;
        return encoded;
    }

    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Example options:<pre>
     *   GZIP: gzip-compresses object before encoding it.
     *   DO_BREAK_LINES: break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP )</code> or
     * <p>
     * Example: <code>encodeBytes( myData, Base64.GZIP | Base64.DO_BREAK_LINES )</code>
     *
     *
     * <p>As of v 2.3, if there is an error with the GZIP stream,
     * the method will throw an java.io.IOException. <b>This is new to v2.3!</b> In earlier versions,
     * it just returned a null value, but in retrospect that's a pretty poor way to handle it.</p>
     *
     * @param source  The data to convert
     * @param off     Offset in array where conversion should begin
     * @param len     Length of data to convert
     * @param options Specified options
     * @return The Base64-encoded data as a String
     * @throws java.io.IOException      if there is an error
     * @throws IllegalArgumentException if source array is null, if source array, offset, or length
     *                                  are invalid
     * @see Base64#GZIP
     * @see Base64#DO_BREAK_LINES
     * @since 2.0
     */
    public static String encodeBytes(byte[] source, int off, int len, int options)
            throws java.io.IOException {
        byte[] encoded = encodeBytesToBytes(source, off, len, options);

        try {
            return new String(encoded, PREFERRED_ENCODING);
        } catch (java.io.UnsupportedEncodingException uue) {
            return new String(encoded);
        }
    }

    /**
     * Similar to {@link #encodeBytes(byte[], int, int, int)} but returns a byte array instead of
     * instantiating a String. This is more efficient if you're working with I/O streams and have
     * large data sets to encode.
     *
     * @param source  The data to convert
     * @param off     Offset in array where conversion should begin
     * @param len     Length of data to convert
     * @param options Specified options
     * @return The Base64-encoded data as a String
     * @throws java.io.IOException      if there is an error
     * @throws IllegalArgumentException if source array is null, if source array, offset, or length
     *                                  are invalid
     * @see Base64#GZIP
     * @see Base64#DO_BREAK_LINES
     * @since 2.3.1
     */
    public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) throws java.io.IOException {
        if (source == null) {
            throw new IllegalArgumentException("Cannot serialize a null array.");
        }
        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }
        if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        }
        if (off + len > source.length) {
            throw new IllegalArgumentException(
                    String.format("Cannot have offset of %d and length of %d with array of length %d", off, len, source.length));
        }
        if ((options & GZIP) != 0) {
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                 OutputStream b64os = new OutputStream(baos, ENCODE | options);
                 java.util.zip.GZIPOutputStream gzos = new java.util.zip.GZIPOutputStream(b64os);) {
                gzos.write(source, off, len);
                return baos.toByteArray();
            } catch (java.io.IOException e) {
                throw e;
            }
        }
        // Else, don't compress. Better not to use streams at all then.
        else {
            boolean breakLines = (options & DO_BREAK_LINES) != 0;


            int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0);
            if (breakLines) {
                encLen += encLen / MAX_LINE_LENGTH;
            }
            byte[] outBuff = new byte[encLen];

            int d = 0;
            int e = 0;
            int len2 = len - 2;
            int lineLength = 0;
            for (; d < len2; d += 3, e += 4) {
                encode3to4(source, d + off, 3, outBuff, e, options);

                lineLength += 4;
                if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                    outBuff[e + 4] = NEW_LINE;
                    e++;
                    lineLength = 0;
                }
            }
            if (d < len) {
                encode3to4(source, d + off, len - d, outBuff, e, options);
                e += 4;
            }
            if (e <= outBuff.length - 1) {
                byte[] finalOut = new byte[e];
                System.arraycopy(outBuff, 0, finalOut, 0, e);
                return finalOut;
            } else {
                return outBuff;
            }

        }
    }




    /* ********  D E C O D I N G   M E T H O D S  ******** */


    /**
     * Decodes four bytes from array <var>source</var> and writes the resulting bytes (up to three of
     * them) to <var>destination</var>. The source and destination arrays can be manipulated anywhere
     * along their length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays are large enough to accommodate
     * <var>srcOffset</var> + 4 for the <var>source</var> array or <var>destOffset</var> + 3 for the
     * <var>destination</var> array. This method returns the actual number of bytes that were
     * converted from the Base64 encoding.
     * <p>This is the lowest level of the decoding methods with
     * all possible parameters.</p>
     *
     * @param source      the array to convert
     * @param srcOffset   the index where conversion begins
     * @param destination the array to hold the conversion
     * @param destOffset  the index where output will be put
     * @param options     alphabet type is pulled from this (standard, url-safe, ordered)
     * @return the number of decoded bytes converted
     * @throws IllegalArgumentException if source or destination arrays are null, if srcOffset or
     *                                  destOffset are invalid or there is not enough room in the
     *                                  array.
     * @since 1.3
     */
    private static int decode4to3(
            byte[] source, int srcOffset,
            byte[] destination, int destOffset, int options) {

        if (source == null) {
            throw new IllegalArgumentException("Source array was null.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(String.format(
                    "Source array with length %d cannot have offset of %d and still process four bytes.",
                    source.length, srcOffset));
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(String.format(
                    "Destination array with length %d cannot have offset of %d and still store three bytes.",
                    destination.length, destOffset));
        }
        final byte[] decodabet = getDecodabet(options);

        if (source[srcOffset + 2] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            int outBuff = ((decodabet[source[srcOffset]] & 0xFF) << 18)
                    | ((decodabet[source[srcOffset + 1]] & 0xFF) << 12);

            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        } else if (source[srcOffset + 3] == EQUALS_SIGN) {
            // Two ways to do the same thing. Don't know which way I like best.
            int outBuff = ((decodabet[source[srcOffset]] & 0xFF) << 18)
                    | ((decodabet[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((decodabet[source[srcOffset + 2]] & 0xFF) << 6);

            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        } else {
            // Two ways to do the same thing. Don't know which way I like best.
            int outBuff = ((decodabet[source[srcOffset]] & 0xFF) << 18)
                    | ((decodabet[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((decodabet[source[srcOffset + 2]] & 0xFF) << 6)
                    | ((decodabet[source[srcOffset + 3]] & 0xFF));

            destination[destOffset] = (byte) (outBuff >> 16);
            destination[destOffset + 1] = (byte) (outBuff >> 8);
            destination[destOffset + 2] = (byte) (outBuff);

            return 3;
        }
    }

    /**
     * A {@link OutputStream} will write data to another
     * <tt>java.io.OutputStream</tt>, given in the constructor,
     * and encode/decode to/from Base64 notation on the fly.
     *
     * @see Base64
     * @since 1.3
     */
    public static class OutputStream extends java.io.FilterOutputStream {

        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] decodabet;

        /**
         * Constructs a {@link OutputStream} in ENCODE mode.
         *
         * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
         * @since 1.3
         */
        public OutputStream(java.io.OutputStream out) {
            this(out, ENCODE);
        }

        /**
         * Constructs a {@link OutputStream} in either ENCODE or DECODE mode.
         * <p>
         * Valid options:<pre>
         *   ENCODE or DECODE: Encode or Decode as data is read.
         *   DO_BREAK_LINES: don't break lines at 76 characters
         *     <i>(only meaningful when encoding)</i>
         * </pre>
         * <p>
         * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
         *
         * @param out     the <tt>java.io.OutputStream</tt> to which data will be written.
         * @param options Specified options.
         * @see Base64#ENCODE
         * @see Base64#DO_BREAK_LINES
         * @since 1.3
         */
        public OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & DO_BREAK_LINES) != 0;
            this.encode = (options & ENCODE) != 0;
            this.bufferLength = encode ? 3 : 4;
            this.buffer = new byte[bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.decodabet = getDecodabet(options);
        }

        /**
         * Writes the byte to the output stream after converting to/from Base64 notation. When encoding,
         * bytes are buffered three at a time before the output stream actually gets a write() call.
         * When decoding, bytes are buffered four at a time.
         *
         * @param theByte the byte to write
         * @since 1.3
         */
        @Override
        public void write(int theByte)
                throws java.io.IOException {
            if (suspendEncoding) {
                this.out.write(theByte);
                return;
            }
            if (encode) {
                buffer[position++] = (byte) theByte;
                if (position >= bufferLength) {
                    this.out.write(encode3to4(b4, buffer, bufferLength, options));

                    lineLength += 4;
                    if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                        this.out.write(NEW_LINE);
                        lineLength = 0;
                    }
                    position = 0;
                }
            } else {
                if (decodabet[theByte & 0x7f] > WHITE_SPACE_ENC) {
                    buffer[position++] = (byte) theByte;
                    if (position >= bufferLength) {
                        int len = Base64.decode4to3(buffer, 0, b4, 0, options);
                        out.write(b4, 0, len);
                        position = 0;
                    }
                } else if (decodabet[theByte & 0x7f] != WHITE_SPACE_ENC) {
                    throw new java.io.IOException("Invalid character in Base64 data.");
                }
            }
        }

        /**
         * Calls {@link #write(int)} repeatedly until <var>len</var> bytes are written.
         *
         * @param theBytes array from which to read bytes
         * @param off      offset for array
         * @param len      max number of bytes to read into array
         * @since 1.3
         */
        @Override
        public void write(byte[] theBytes, int off, int len)
                throws java.io.IOException {
            if (suspendEncoding) {
                this.out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; i++) {
                write(theBytes[off + i]);
            }
        }

        /**
         * Method added by PHIL. [Thanks, PHIL. -Rob] This pads the buffer without closing the stream.
         *
         * @throws java.io.IOException if there's an error.
         */
        public void flushBase64() throws java.io.IOException {
            if (position > 0) {
                if (encode) {
                    out.write(encode3to4(b4, buffer, position, options));
                    position = 0;
                } else {
                    throw new java.io.IOException("Base64 input not properly padded.");
                }
            }
        }

        /**
         * Flushes and closes (I think, in the superclass) the stream.
         *
         * @since 1.3
         */
        @Override
        public void close() throws java.io.IOException {
            flushBase64();

            super.close();

            buffer = null;
            out = null;
        }
    }
}