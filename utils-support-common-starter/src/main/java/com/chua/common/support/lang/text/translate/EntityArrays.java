package com.chua.common.support.lang.text.translate;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class holding various entity data for HTML and XML - generally for use with
 * the LookupTranslator.
 * All Maps are generated using {@code java.util.Collections.unmodifiableMap()}.
 *
 * @author Administrator
 * @since 1.0
 */
public class EntityArrays {

    /**
     * A Map&lt;CharSequence, CharSequence&gt; to to escape
     * <a href="https:
     * characters to their named HTML 3.x equivalents.
     */
    public static final Map<CharSequence, CharSequence> ISO8859_1_ESCAPE;

    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        initialMap.put("\u00A0", "&nbsp;"); 
        initialMap.put("\u00A1", "&iexcl;"); 
        initialMap.put("\u00A2", "&cent;"); 
        initialMap.put("\u00A3", "&pound;"); 
        initialMap.put("\u00A4", "&curren;"); 
        initialMap.put("\u00A5", "&yen;"); 
        initialMap.put("\u00A6", "&brvbar;"); 
        initialMap.put("\u00A7", "&sect;"); 
        initialMap.put("\u00A8", "&uml;");
        initialMap.put("\u00A9", "&copy;"); 
        initialMap.put("\u00AA", "&ordf;"); 
        initialMap.put("\u00AB", "&laquo;"); 
        initialMap.put("\u00AC", "&not;"); 
        initialMap.put("\u00AD", "&shy;"); 
        initialMap.put("\u00AE", "&reg;"); 
        initialMap.put("\u00AF", "&macr;"); 
        initialMap.put("\u00B0", "&deg;"); 
        initialMap.put("\u00B1", "&plusmn;"); 
        initialMap.put("\u00B2", "&sup2;"); 
        initialMap.put("\u00B3", "&sup3;"); 
        initialMap.put("\u00B4", "&acute;"); 
        initialMap.put("\u00B5", "&micro;"); 
        initialMap.put("\u00B6", "&para;"); 
        initialMap.put("\u00B7", "&middot;"); 
        initialMap.put("\u00B8", "&cedil;"); 
        initialMap.put("\u00B9", "&sup1;"); 
        initialMap.put("\u00BA", "&ordm;"); 
        initialMap.put("\u00BB", "&raquo;"); 
        initialMap.put("\u00BC", "&frac14;"); 
        initialMap.put("\u00BD", "&frac12;"); 
        initialMap.put("\u00BE", "&frac34;"); 
        initialMap.put("\u00BF", "&iquest;"); 
        initialMap.put("\u00C0", "&Agrave;"); 
        initialMap.put("\u00C1", "&Aacute;"); 
        initialMap.put("\u00C2", "&Acirc;"); 
        initialMap.put("\u00C3", "&Atilde;"); 
        initialMap.put("\u00C4", "&Auml;"); 
        initialMap.put("\u00C5", "&Aring;"); 
        initialMap.put("\u00C6", "&AElig;"); 
        initialMap.put("\u00C7", "&Ccedil;"); 
        initialMap.put("\u00C8", "&Egrave;"); 
        initialMap.put("\u00C9", "&Eacute;"); 
        initialMap.put("\u00CA", "&Ecirc;"); 
        initialMap.put("\u00CB", "&Euml;"); 
        initialMap.put("\u00CC", "&Igrave;"); 
        initialMap.put("\u00CD", "&Iacute;"); 
        initialMap.put("\u00CE", "&Icirc;"); 
        initialMap.put("\u00CF", "&Iuml;"); 
        initialMap.put("\u00D0", "&ETH;"); 
        initialMap.put("\u00D1", "&Ntilde;"); 
        initialMap.put("\u00D2", "&Ograve;"); 
        initialMap.put("\u00D3", "&Oacute;"); 
        initialMap.put("\u00D4", "&Ocirc;"); 
        initialMap.put("\u00D5", "&Otilde;"); 
        initialMap.put("\u00D6", "&Ouml;"); 
        initialMap.put("\u00D7", "&times;"); 
        initialMap.put("\u00D8", "&Oslash;"); 
        initialMap.put("\u00D9", "&Ugrave;"); 
        initialMap.put("\u00DA", "&Uacute;"); 
        initialMap.put("\u00DB", "&Ucirc;"); 
        initialMap.put("\u00DC", "&Uuml;"); 
        initialMap.put("\u00DD", "&Yacute;"); 
        initialMap.put("\u00DE", "&THORN;"); 
        initialMap.put("\u00DF", "&szlig;"); 
        initialMap.put("\u00E0", "&agrave;"); 
        initialMap.put("\u00E1", "&aacute;"); 
        initialMap.put("\u00E2", "&acirc;"); 
        initialMap.put("\u00E3", "&atilde;"); 
        initialMap.put("\u00E4", "&auml;"); 
        initialMap.put("\u00E5", "&aring;"); 
        initialMap.put("\u00E6", "&aelig;"); 
        initialMap.put("\u00E7", "&ccedil;"); 
        initialMap.put("\u00E8", "&egrave;"); 
        initialMap.put("\u00E9", "&eacute;"); 
        initialMap.put("\u00EA", "&ecirc;"); 
        initialMap.put("\u00EB", "&euml;"); 
        initialMap.put("\u00EC", "&igrave;"); 
        initialMap.put("\u00ED", "&iacute;"); 
        initialMap.put("\u00EE", "&icirc;"); 
        initialMap.put("\u00EF", "&iuml;"); 
        initialMap.put("\u00F0", "&eth;"); 
        initialMap.put("\u00F1", "&ntilde;"); 
        initialMap.put("\u00F2", "&ograve;"); 
        initialMap.put("\u00F3", "&oacute;"); 
        initialMap.put("\u00F4", "&ocirc;"); 
        initialMap.put("\u00F5", "&otilde;"); 
        initialMap.put("\u00F6", "&ouml;"); 
        initialMap.put("\u00F7", "&divide;"); 
        initialMap.put("\u00F8", "&oslash;"); 
        initialMap.put("\u00F9", "&ugrave;"); 
        initialMap.put("\u00FA", "&uacute;"); 
        initialMap.put("\u00FB", "&ucirc;"); 
        initialMap.put("\u00FC", "&uuml;"); 
        initialMap.put("\u00FD", "&yacute;"); 
        initialMap.put("\u00FE", "&thorn;"); 
        initialMap.put("\u00FF", "&yuml;"); 
        ISO8859_1_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    /**
     * Reverse of {@link #ISO8859_1_ESCAPE} for unescaping purposes.
     */
    public static final Map<CharSequence, CharSequence> ISO8859_1_UNESCAPE;

    static {
        ISO8859_1_UNESCAPE = Collections.unmodifiableMap(invert(ISO8859_1_ESCAPE));
    }

    /**
     * A Map&lt;CharSequence, CharSequence&gt; to escape additional
     * <a href="http:
     * references</a>. Note that this must be used with {@link #ISO8859_1_ESCAPE} to get the full list of
     * HTML 4.0 character entities.
     */
    public static final Map<CharSequence, CharSequence> HTML40_EXTENDED_ESCAPE;

    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        
        initialMap.put("\u0192", "&fnof;"); 
        
        initialMap.put("\u0391", "&Alpha;"); 
        initialMap.put("\u0392", "&Beta;"); 
        initialMap.put("\u0393", "&Gamma;"); 
        initialMap.put("\u0394", "&Delta;"); 
        initialMap.put("\u0395", "&Epsilon;"); 
        initialMap.put("\u0396", "&Zeta;"); 
        initialMap.put("\u0397", "&Eta;"); 
        initialMap.put("\u0398", "&Theta;"); 
        initialMap.put("\u0399", "&Iota;"); 
        initialMap.put("\u039A", "&Kappa;"); 
        initialMap.put("\u039B", "&Lambda;"); 
        initialMap.put("\u039C", "&Mu;"); 
        initialMap.put("\u039D", "&Nu;"); 
        initialMap.put("\u039E", "&Xi;"); 
        initialMap.put("\u039F", "&Omicron;"); 
        initialMap.put("\u03A0", "&Pi;"); 
        initialMap.put("\u03A1", "&Rho;"); 
        
        initialMap.put("\u03A3", "&Sigma;"); 
        initialMap.put("\u03A4", "&Tau;"); 
        initialMap.put("\u03A5", "&Upsilon;"); 
        initialMap.put("\u03A6", "&Phi;"); 
        initialMap.put("\u03A7", "&Chi;"); 
        initialMap.put("\u03A8", "&Psi;"); 
        initialMap.put("\u03A9", "&Omega;"); 
        initialMap.put("\u03B1", "&alpha;"); 
        initialMap.put("\u03B2", "&beta;"); 
        initialMap.put("\u03B3", "&gamma;"); 
        initialMap.put("\u03B4", "&delta;"); 
        initialMap.put("\u03B5", "&epsilon;"); 
        initialMap.put("\u03B6", "&zeta;"); 
        initialMap.put("\u03B7", "&eta;"); 
        initialMap.put("\u03B8", "&theta;"); 
        initialMap.put("\u03B9", "&iota;"); 
        initialMap.put("\u03BA", "&kappa;"); 
        initialMap.put("\u03BB", "&lambda;"); 
        initialMap.put("\u03BC", "&mu;"); 
        initialMap.put("\u03BD", "&nu;"); 
        initialMap.put("\u03BE", "&xi;"); 
        initialMap.put("\u03BF", "&omicron;"); 
        initialMap.put("\u03C0", "&pi;"); 
        initialMap.put("\u03C1", "&rho;"); 
        initialMap.put("\u03C2", "&sigmaf;"); 
        initialMap.put("\u03C3", "&sigma;"); 
        initialMap.put("\u03C4", "&tau;"); 
        initialMap.put("\u03C5", "&upsilon;"); 
        initialMap.put("\u03C6", "&phi;"); 
        initialMap.put("\u03C7", "&chi;"); 
        initialMap.put("\u03C8", "&psi;"); 
        initialMap.put("\u03C9", "&omega;"); 
        initialMap.put("\u03D1", "&thetasym;"); 
        initialMap.put("\u03D2", "&upsih;"); 
        initialMap.put("\u03D6", "&piv;"); 
        
        initialMap.put("\u2022", "&bull;"); 
        
        initialMap.put("\u2026", "&hellip;"); 
        initialMap.put("\u2032", "&prime;"); 
        initialMap.put("\u2033", "&Prime;"); 
        initialMap.put("\u203E", "&oline;"); 
        initialMap.put("\u2044", "&frasl;"); 
        
        initialMap.put("\u2118", "&weierp;"); 
        initialMap.put("\u2111", "&image;"); 
        initialMap.put("\u211C", "&real;"); 
        initialMap.put("\u2122", "&trade;"); 
        initialMap.put("\u2135", "&alefsym;"); 
        
        
        
        initialMap.put("\u2190", "&larr;"); 
        initialMap.put("\u2191", "&uarr;"); 
        initialMap.put("\u2192", "&rarr;"); 
        initialMap.put("\u2193", "&darr;"); 
        initialMap.put("\u2194", "&harr;"); 
        initialMap.put("\u21B5", "&crarr;"); 
        initialMap.put("\u21D0", "&lArr;"); 
        
        
        
        initialMap.put("\u21D1", "&uArr;"); 
        initialMap.put("\u21D2", "&rArr;"); 
        
        
        
        initialMap.put("\u21D3", "&dArr;"); 
        initialMap.put("\u21D4", "&hArr;"); 
        
        initialMap.put("\u2200", "&forall;"); 
        initialMap.put("\u2202", "&part;"); 
        initialMap.put("\u2203", "&exist;"); 
        initialMap.put("\u2205", "&empty;"); 
        initialMap.put("\u2207", "&nabla;"); 
        initialMap.put("\u2208", "&isin;"); 
        initialMap.put("\u2209", "&notin;"); 
        initialMap.put("\u220B", "&ni;"); 
        
        initialMap.put("\u220F", "&prod;"); 
        
        
        initialMap.put("\u2211", "&sum;"); 
        
        
        initialMap.put("\u2212", "&minus;"); 
        initialMap.put("\u2217", "&lowast;"); 
        initialMap.put("\u221A", "&radic;"); 
        initialMap.put("\u221D", "&prop;"); 
        initialMap.put("\u221E", "&infin;"); 
        initialMap.put("\u2220", "&ang;"); 
        initialMap.put("\u2227", "&and;"); 
        initialMap.put("\u2228", "&or;"); 
        initialMap.put("\u2229", "&cap;"); 
        initialMap.put("\u222A", "&cup;"); 
        initialMap.put("\u222B", "&int;"); 
        initialMap.put("\u2234", "&there4;"); 
        initialMap.put("\u223C", "&sim;"); 
        
        
        initialMap.put("\u2245", "&cong;"); 
        initialMap.put("\u2248", "&asymp;"); 
        initialMap.put("\u2260", "&ne;"); 
        initialMap.put("\u2261", "&equiv;"); 
        initialMap.put("\u2264", "&le;"); 
        initialMap.put("\u2265", "&ge;"); 
        initialMap.put("\u2282", "&sub;"); 
        initialMap.put("\u2283", "&sup;"); 
        
        
        
        initialMap.put("\u2284", "&nsub;"); 
        initialMap.put("\u2286", "&sube;"); 
        initialMap.put("\u2287", "&supe;"); 
        initialMap.put("\u2295", "&oplus;"); 
        initialMap.put("\u2297", "&otimes;"); 
        initialMap.put("\u22A5", "&perp;"); 
        initialMap.put("\u22C5", "&sdot;"); 
        
        
        initialMap.put("\u2308", "&lceil;"); 
        initialMap.put("\u2309", "&rceil;"); 
        initialMap.put("\u230A", "&lfloor;"); 
        initialMap.put("\u230B", "&rfloor;"); 
        initialMap.put("\u2329", "&lang;"); 
        
        
        initialMap.put("\u232A", "&rang;"); 
        
        
        
        initialMap.put("\u25CA", "&loz;"); 
        
        initialMap.put("\u2660", "&spades;"); 
        
        initialMap.put("\u2663", "&clubs;"); 
        initialMap.put("\u2665", "&hearts;"); 
        initialMap.put("\u2666", "&diams;"); 

        
        initialMap.put("\u0152", "&OElig;"); 
        initialMap.put("\u0153", "&oelig;"); 
        
        initialMap.put("\u0160", "&Scaron;"); 
        initialMap.put("\u0161", "&scaron;"); 
        initialMap.put("\u0178", "&Yuml;"); 
        
        initialMap.put("\u02C6", "&circ;"); 
        initialMap.put("\u02DC", "&tilde;"); 
        
        initialMap.put("\u2002", "&ensp;"); 
        initialMap.put("\u2003", "&emsp;"); 
        initialMap.put("\u2009", "&thinsp;"); 
        initialMap.put("\u200C", "&zwnj;"); 
        initialMap.put("\u200D", "&zwj;"); 
        initialMap.put("\u200E", "&lrm;"); 
        initialMap.put("\u200F", "&rlm;"); 
        initialMap.put("\u2013", "&ndash;"); 
        initialMap.put("\u2014", "&mdash;"); 
        initialMap.put("\u2018", "&lsquo;"); 
        initialMap.put("\u2019", "&rsquo;"); 
        initialMap.put("\u201A", "&sbquo;"); 
        initialMap.put("\u201C", "&ldquo;"); 
        initialMap.put("\u201D", "&rdquo;"); 
        initialMap.put("\u201E", "&bdquo;"); 
        initialMap.put("\u2020", "&dagger;"); 
        initialMap.put("\u2021", "&Dagger;"); 
        initialMap.put("\u2030", "&permil;"); 
        initialMap.put("\u2039", "&lsaquo;"); 
        
        initialMap.put("\u203A", "&rsaquo;"); 
        
        initialMap.put("\u20AC", "&euro;"); 
        HTML40_EXTENDED_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    /**
     * Reverse of {@link #HTML40_EXTENDED_ESCAPE} for unescaping purposes.
     */
    public static final Map<CharSequence, CharSequence> HTML40_EXTENDED_UNESCAPE;

    static {
        HTML40_EXTENDED_UNESCAPE = Collections.unmodifiableMap(invert(HTML40_EXTENDED_ESCAPE));
    }

    /**
     * A Map&lt;CharSequence, CharSequence&gt; to escape the basic XML and HTML
     * character entities.
     * <p>
     * Namely: {@code " & < >}
     */
    public static final Map<CharSequence, CharSequence> BASIC_ESCAPE;

    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        initialMap.put("\"", "&quot;"); 
        initialMap.put("&", "&amp;");   
        initialMap.put("<", "&lt;");    
        initialMap.put(">", "&gt;");    
        BASIC_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    /**
     * Reverse of {@link #BASIC_ESCAPE} for unescaping purposes.
     */
    public static final Map<CharSequence, CharSequence> BASIC_UNESCAPE;

    static {
        BASIC_UNESCAPE = Collections.unmodifiableMap(invert(BASIC_ESCAPE));
    }

    /**
     * A Map&lt;CharSequence, CharSequence&gt; to escape the apostrophe character to
     * its XML character entity.
     */
    public static final Map<CharSequence, CharSequence> APOS_ESCAPE;

    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        initialMap.put("'", "&apos;"); 
        APOS_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    /**
     * Reverse of {@link #APOS_ESCAPE} for unescaping purposes.
     */
    public static final Map<CharSequence, CharSequence> APOS_UNESCAPE;

    static {
        APOS_UNESCAPE = Collections.unmodifiableMap(invert(APOS_ESCAPE));
    }

    /**
     * A Map&lt;CharSequence, CharSequence&gt; to escape the Java
     * control characters.
     * <p>
     * Namely: {@code \b \n \t \f \r}
     */
    public static final Map<CharSequence, CharSequence> JAVA_CTRL_CHARS_ESCAPE;

    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        initialMap.put("\b", "\\b");
        initialMap.put("\n", "\\n");
        initialMap.put("\t", "\\t");
        initialMap.put("\f", "\\f");
        initialMap.put("\r", "\\r");
        JAVA_CTRL_CHARS_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    /**
     * Reverse of {@link #JAVA_CTRL_CHARS_ESCAPE} for unescaping purposes.
     */
    public static final Map<CharSequence, CharSequence> JAVA_CTRL_CHARS_UNESCAPE;

    static {
        JAVA_CTRL_CHARS_UNESCAPE = Collections.unmodifiableMap(invert(JAVA_CTRL_CHARS_ESCAPE));
    }

    /**
     * Inverts an escape Map into an unescape Map.
     *
     * @param map Map&lt;String, String&gt; to be inverted
     * @return Map&lt;String, String&gt; inverted array
     */
    public static Map<CharSequence, CharSequence> invert(final Map<CharSequence, CharSequence> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
    }

}
