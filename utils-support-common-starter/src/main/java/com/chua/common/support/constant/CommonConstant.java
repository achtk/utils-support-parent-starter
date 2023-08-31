package com.chua.common.support.constant;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 常量
 *
 * @author ACHTK
 */
public final class CommonConstant extends NameConstant{
    public static final String WILDCARD = "*";
    public static final int INVALID_EXITVALUE = 0xdeadbeef;

    public static final int MAX_HTTP_CONTENT_LENGTH = 1024 * 1024 * 10;
    public static final int MAX_CACHED_BUILDER_SIZE = 8 * 1024;
    public static final int MAX_IDLE_BUILDERS = NumberConstant.EIGHT;
    /**
     * -1
     */
    public static final String SYMBOL_EOF = "-1";
    /**
     * @
     */
    public static final String SYMBOL_AT = "@";
    /**
     * @
     */
    public static final char SYMBOL_AT_CHAR = '@';
    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;
    /**
     * 默认中等缓存大小 16384
     */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 2 << 13;
    /**
     * 默认大缓存大小 32768
     */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 2 << 14;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    public static final DecimalFormat FORMAT = new DecimalFormat("###.000");
    public static final String[] EMPTY_ARRAY = new String[0];
    public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    public static final String[] EMPTY_STRING_ARRAY = EMPTY_ARRAY;
    public static final Integer[] EMPTY_INTEGER = new Integer[0];
    public static final byte[] EMPTY_BYTE = new byte[0];
    public static final Object[] EMPTY_OBJECT = new Object[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = EMPTY_OBJECT;
    public static final Class<?>[] EMPTY_CLASS = new Class<?>[0];
    /**
     * 字母开头
     */
    public static final Pattern START_WITH_WORD_REGEX = Pattern.compile("^[A-Za-z].*");
    /**
     * 纯数字
     */
    public static final Pattern NUMERIC_REGEX = Pattern.compile("[0-9]+");
    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    public static final int PAD_LIMIT = 8192;
    public static final String EMPTY = "";
    public static final int TWE = NumberConstant.TWE;
    /**
     * os名称
     */
    public static final String OS_NAME = System.getProperty("os.name");
    /**
     * {}
     */
    public static final String EMPTY_JSON = "{}";
    /**
     * ""
     */
    public static final String SYMBOL_EMPTY = "";
    /**
     * [
     */
    public static final String SYMBOL_LEFT_SQUARE_BRACKET = "[";
    /**
     * [
     */
    public static final char SYMBOL_LEFT_SQUARE_BRACKET_CHAR = '[';
    /**
     * ]
     */
    public static final char SYMBOL_RIGHT_SQUARE_BRACKET_CHAR = ']';
    /**
     * <
     */
    public static final String SYMBOL_LEFT_TRIANGLE_BRACKET = "<";
    /**
     * ]
     */
    public static final String SYMBOL_RIGHT_SQUARE_BRACKET = "]";
    /**
     * $
     */
    public static final String SYMBOL_DOLLAR = "$";
    /**
     * $
     */
    public static final char SYMBOL_DOLLAR_CHAR = '$';
    /**
     * $
     */
    public static final String SYMBOL_XPATH = "$.";
    /**
     * " "
     */
    public static final String SYMBOL_BLANK = " ";
    /**
     * " "
     */
    public static final String SYMBOL_SPACE = SYMBOL_BLANK;
    /**
     * .
     */
    public static final String SYMBOL_DOT = ".";
    /**
     * AS
     */
    public static final String SYMBOL_AS = " AS ";
    /**
     * ..
     */
    public static final String SYMBOL_DOUBLE_DOT = "..";
    /**
     * .*
     */
    public static final String SYMBOL_DOT_ASTERISK = ".*";
    /**
     * /
     */
    public static final String SYMBOL_LEFT_SLASH = "/";
    /**
     * #/
     */
    public static final String SYMBOL_LEFT_HASH_SLASH = "#/";
    /**
     * "
     */
    public static final String SYMBOL_QUOTE = "\"";
    /**
     * \n
     */
    public static final String SYMBOL_NEWLINE = "\n";
    /**
     * #{
     */
    public static final String SYMBOL_HASH_LEFT_BRACE = "#{";
    /**
     * ${
     */
    public static final String SYMBOL_DOLLAR_LEFT_BRACE = "${";
    /**
     * "
     */
    public static final char SYMBOL_QUOTE_CHAR = '"';
    /**
     * #
     */
    public static final String SYMBOL_HASH = "#";
    /**
     * #
     */
    public static final char SYMBOL_HASH_CHAR = '#';
    /**
     * \
     */
    public static final String SYMBOL_RIGHT_SLASH = "\\";
    /**
     * \\'
     */
    public static final char SYMBOL_RIGHT_SLASH_CHAR = '\\';
    /**
     * &&
     */
    public static final String SYMBOL_DOUBLE_AND = "&&";
    /**
     * ||
     */
    public static final String SYMBOL_DOUBLE_PIPE = "||";
    /**
     * \'
     */
    public static final char SYMBOL_RIGHT_ONE_SLASH_CHAR = '\'';
    /**
     * ,
     */
    public static final String SYMBOL_COMMA = ",";
    /**
     * ,
     */
    public static final char SYMBOL_COMMA_CHAR = ',';
    /**
     * ;
     */
    public static final String SYMBOL_SEMICOLON = ";";
    /**
     * ;
     */
    public static final char SYMBOL_SEMICOLON_CHAR = ';';
    /**
     * :
     */
    public static final String SYMBOL_COLON = ":";
    /**
     * -
     */
    public static final String SYMBOL_MINUS = "-";
    /**
     * -
     */
    public static final char SYMBOL_MINUS_CHAR = '-';
    /**
     * +
     */
    public static final String SYMBOL_PLUS = "+";
    /**
     * +
     */
    public static final char SYMBOL_PLUS_CHAR = '+';
    /**
     * \r
     */
    public static final char SYMBOL_R_CHAR = '\r';
    /**
     * \n
     */
    public static final char SYMBOL_N_CHAR = '\n';
    /**
     * _
     */
    public static final String SYMBOL_UNDERLINE = "_";
    /**
     * _
     */
    public static final String SYMBOL_UNDERSCORE = SYMBOL_UNDERLINE;
    /**
     * -
     */
    public static final String SYMBOL_MINS = "-";
    /**
     * =
     */
    public static final String SYMBOL_EQUALS = "=";
    /**
     * =
     */
    public static final char SYMBOL_EQUALS_CHAR = '=';
    /**
     * >
     */
    public static final String SYMBOL_RIGHT_CHEV = ">";
    /**
     * <
     */
    public static final String SYMBOL_LEFT_CHEV = "<";
    /**
     * %
     */
    public static final String SYMBOL_PERCENT = "%";
    /**
     * &
     */
    public static final char SYMBOL_AND_CHAR = '&';
    /**
     * &
     */
    public static final String SYMBOL_AND = "&";
    /**
     * \n
     */
    public static final String SYMBOL_N = "\n";
    /**
     * true
     */
    public static final String TRUE = "true";
    /**
     * false
     */
    public static final String FALSE = "false";
    /**
     * \t
     */
    public static final char SYMBOL_T_CHAR = '\t';
    /**
     * \0
     */
    public static final char SYMBOL_NULL_CHAR = '\0';
    /**
     * ' '
     */
    public static final char SYMBOL_BLANK_CHAR = ' ';
    /**
     * )
     */
    public static final String SYMBOL_RIGHT_BRACKETS = ")";
    /**
     * )
     */
    public static final String SYMBOL_RIGHT_BRACKET = SYMBOL_RIGHT_BRACKETS;
    /**
     * (
     */
    public static final String SYMBOL_LEFT_BRACKETS = "(";
    /**
     * (
     */
    public static final char SYMBOL_LEFT_BRACKETS_CHAR = '(';
    /**
     * )
     */
    public static final char SYMBOL_RIGHT_BRACKETS_CHAR = ')';
    /**
     * (
     */
    public static final String SYMBOL_LEFT_BRACKET = SYMBOL_LEFT_BRACKETS;
    /**
     * *
     */
    public static final String SYMBOL_ASTERISK = "*";
    /**
     * **
     */
    public static final String SYMBOL_ASTERISK_ANY = "**";
    /**
     * ?
     */
    public static final String SYMBOL_QUESTION = "?";
    /**
     * {
     */
    public static final String SYMBOL_LEFT_BIG_PARENTHESES = "{";
    /**
     * }
     */
    public static final String SYMBOL_RIGHT_BIG_PARENTHESES = "}";

    /**
     * }
     */
    public static final String SYMBOL_RIGHT_BRACE = SYMBOL_RIGHT_BIG_PARENTHESES;

    /**
     * }
     */
    public static final String SYMBOL_LEFT_BRACE = SYMBOL_LEFT_BIG_PARENTHESES;

    /**
     * ~
     */
    public static final String SYMBOL_WAVY_LINE = "~";
    //*****************************************char******************************************/
    /**
     * .
     */
    public static final char SYMBOL_DOT_CHAR = '.';
    /**
     * /
     */
    public static final char SYMBOL_LEFT_SLASH_CHAR = '/';
    /**
     * '?'
     */
    public static final char SYMBOL_QUESTION_CHAR = '?';
    /**
     * {
     */
    public static final char SYMBOL_LEFT_BIG_PARANTHESES_CHAR = '{';
    /**
     * }
     */
    public static final char SYMBOL_RIGHT_BIG_PARANTHESES_CHAR = '}';
    /**
     * :
     */
    public static final char SYMBOL_COLON_CHAR = ':';
    /**
     * ::
     */
    public static final String SYMBOL_DOUBLE_COLON = "::";
    /**
     * ~
     */
    public static final char SYMBOL_WAVY_LINE_CHAR = '~';
    /**
     * *
     */
    public static final char SYMBOL_ASTERISK_CHAR = '*';
    /**
     * '0'
     */
    public static final char CHARACTER_0 = '0';
    /**
     * '9'
     */
    public static final char CHARACTER_9 = '9';
    /**
     * 'A'
     */
    public static final char LETTER_UPPERCASE_A = 'A';
    /**
     * 'C'
     */
    public static final char LETTER_UPPERCASE_C = 'C';
    /**
     * V
     */
    public static final char LETTER_UPPERCASE_V = 'V';
    /**
     * D
     */
    public static final char LETTER_UPPERCASE_D = 'D';
    /**
     * J
     */
    public static final char LETTER_UPPERCASE_J = 'J';
    /**
     * O
     */
    public static final char LETTER_UPPERCASE_O = 'O';
    /**
     * N
     */
    public static final char LETTER_UPPERCASE_N = 'N';
    /**
     * I
     */
    public static final char LETTER_UPPERCASE_I = 'I';
    /**
     * W
     */
    public static final char LETTER_UPPERCASE_W = 'W';
    /**
     * Y
     */
    public static final char LETTER_UPPERCASE_Y = 'Y';
    /**
     * 'L'
     */
    public static final char LETTER_UPPERCASE_L = 'L';
    /**
     * T
     */
    public static final char LETTER_UPPERCASE_T = 'T';
    /**
     * P
     */
    public static final char LETTER_UPPERCASE_P = 'P';
    /**
     * F
     */
    public static final char LETTER_UPPERCASE_F = 'F';
    /**
     * M
     */
    public static final char LETTER_UPPERCASE_M = 'M';
    /**
     * B
     */
    public static final char LETTER_UPPERCASE_B = 'B';
    /**
     * G
     */
    public static final char LETTER_UPPERCASE_G = 'G';
    /**
     * Q
     */
    public static final char LETTER_UPPERCASE_Q = 'Q';
    /**
     * 'Z'
     */
    public static final char LETTER_UPPERCASE_Z = 'Z';
    /**
     * 'k'
     */
    public static final char LETTER_LOWERCASE_K = 'k';
    /**
     * 'i'
     */
    public static final char LETTER_LOWERCASE_I = 'i';
    /**
     * 'o'
     */
    public static final char LETTER_LOWERCASE_O = 'o';
    /**
     * 'a'
     */
    public static final char LETTER_LOWERCASE_A = 'a';
    /**
     * 'f'
     */
    public static final char LETTER_LOWERCASE_F = 'f';
    /**
     * n
     */
    public static final char LETTER_LOWERCASE_N = 'n';
    /**
     * t
     */
    public static final char LETTER_LOWERCASE_T = 't';
    /**
     * r
     */
    public static final char LETTER_LOWERCASE_R = 'r';
    /**
     * e
     */
    public static final char LETTER_LOWERCASE_E = 'e';
    /**
     * 'z'
     */
    public static final char LETTER_LOWERCASE_Z = 'z';
    /**
     * {}
     */
    public static final Properties EMPTY_PROPERTIES = new Properties();

    /**
     * -1
     */
    public static final int INDEX_NOT_FOUND = -1;
    /**
     * path.separator
     */
    public static final String PATH_SEPARATOR = "path.separator";
    /**
     * java.class.path
     */
    public static final String JAVA_CLASS_PATH = "java.class.path";
    /**
     * -------------------------------------格式化-------------------------------------
     */
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.000");
    /**
     * -------------------------------------资源文件-------------------------------------
     */
    public static final String RESOURCE_MESSAGE = "language/message";
    //-------------------------------------基础常量-------------------------------------*/
    /**
     * Used to build output as Hex
     */
    public static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
    public static final char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * ".*\\.jar(\\!.*|$)"
     */
    public static final String JAR_PATTERN = ".*\\.jar(\\!.*|$)";

    /**
     * A
     */
    public static final char LETTER_A = 'A';
    /**
     * F
     */
    public static final char LETTER_F = 'F';
    /**
     * Z
     */
    public static final char LETTER_Z = 'Z';
    /**
     * 0x
     */
    public static final String HEX_16 = "0x";
    /**
     * 0X
     */
    public static final String HEX_16_UPPER = "0X";


    /**
     * 系统语言环境，默认为中文zh
     */
    public static final String LANGUAGE = "zh";
    /**
     * 系统国家环境，默认为中国CN
     */
    public static final String COUNTRY = "CN";
    /**
     * /jre
     */
    public static final String PATH_JRE = "/jre";
    /**
     * sun
     */
    public static final String SUN = "sun";
    /**
     * 0
     */
    public static final String ZERO = "0";
    /**
     * any
     */
    public static final String ANY = "any";
    /**
     * any
     */
    public static final String ANY_PROTOCOL = "any:";
    /**
     * private
     */
    public static final String PRIVATE = "private";
    /**
     * protect
     */
    public static final String PROTECT = "protect";
    /**
     * public
     */
    public static final String PUBLIC = "public";
    /**
     * $javassist
     */
    public static final String JAVASSIST = "$javassist";
    /**
     * javassist
     */
    public static final String NAMED_JAVASSIST = "javassist";
    /**
     * jdk
     */
    public static final String NAMED_JDK = "jdk";
    /**
     * window
     */
    public static final String WINDOW = "window";
    /**
     * linux
     */
    public static final String LINUX = "linux";
    /**
     * class
     */
    public static final String CLASS = "class";
    /**
     * duplicate
     */
    public static final CharSequence DUPLICATE = "duplicate";
    /**
     * GMT
     */
    public static final String GMT = "GMT";
    /**
     * 映射
     */
    public static final String MAPPER = "->";
    /**
     * Pseudo URL prefix for loading from the class path: "leaf:".
     */
    public static final String LEAF_URL_PREFIX = "leaf:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String CLASSPATH_URL_ALL_PREFIX = "classpath*:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String FILE_SYSTEM_URL_PREFIX = "filesystem:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String FILE_SYSTEM_URL_ALL_PREFIX = "filesystem*:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String XPATH_URL_PREFIX = "xpath:";
    /**
     * Pseudo URL prefix for loading from the class path: "subclass:".
     */
    public static final String SUBCLASS_URL_PREFIX = "subclass:";
    /**
     * Pseudo URL prefix for loading from the class path: "class:".
     */
    public static final String CLASS_URL_PREFIX = "class:";
    /**
     * compress:
     */
    public static final String COMPRESS_URL_PREFIX = "compress:";
    /**
     * URL prefix for loading from the file system: "file:".
     */
    public static final String FILE_URL_PREFIX = "file:";
    /**
     * URL prefix for loading from the file system: "file:".
     */
    public static final String FTP_URL_PREFIX = "ftp:";
    /**
     * URL prefix for loading from a jar file: "jar:".
     */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL prefix for loading from a jar file: "wsjar:".
     */
    public static final String WS_JAR_URL_PREFIX = "wsjar:";
    /**
     * URL prefix for loading from a war file on Tomcat: "war:".
     */
    public static final String WAR_URL_PREFIX = "war:";
    /**
     * URL protocol for a file in the file system: "file".
     */
    public static final String URL_PROTOCOL_FILE = FILE;
    /**
     * URL protocol for an entry from a jar file: "jar".
     */
    public static final String URL_PROTOCOL_JAR = JAR;
    /**
     * URL protocol for an entry from a war file: "war".
     */
    public static final String URL_PROTOCOL_WAR = WAR;
    /**
     * URL protocol for an entry from a zip file: "zip".
     */
    public static final String URL_PROTOCOL_ZIP = ZIP;
    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar".
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip".
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL protocol for a JBoss file system resource: "vfsfile".
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL protocol for a general JBoss VFS resource: "vfs".
     */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /**
     * File extension for a regular jar file: ".war!".
     */
    public static final String WAR_FILE_EXTENSION_IN = ".war!";
    /**
     * File extension for a regular jar file: ".war".
     */
    public static final String WAR_FILE_EXTENSION = ".war";
    /**
     * File extension for a regular jar file: ".jar".
     */
    public static final String JAR_FILE_EXTENSION = ".jar";
    /**
     * File extension for a regular jar file: ".jar!".
     */
    public static final String JAR_FILE_EXTENSION_IN = ".jar!";
    /**
     * Separator between JAR URL and file path within the JAR: "!/".
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * Special separator between WAR URL and jar part on Tomcat.
     */
    public static final String WAR_URL_SEPARATOR = "*/";


    public static final String PIC_SOURCE = "png,jpeg,jpg,bmp,tiff,tif";

    public static final String SYMBOL_EMPTY_ARRAY = "[]";
    public static final String SYMBOL_EMPTY_MAP = "{}";
    public static final char LETTER_NIGHT = '9';
    public static final char LETTER_ZERO = '0';
    public static final String UNKNOWN = "unknown";
    /**
     * "already exists"
     */
    public static final CharSequence EXIST_SIGN = "already exists";
    public static final String WHITESPACE = " \n\r\f\t";
    public static final String PREVIEW = "preview";

    public static final String DOWNLOAD = "download";
    public static final String XZ = ".xz";
    public static final String GZ = ".gz";
    public static final String EMPTY_STRING = "";
    public static final String CUT = "[,;]{1}";
    public static final CharSequence POM = "pom.xml";
    /**
     * ' '
     */
    public static final char SYMBOL_SPACE_CHAR = ' ';



    /**
     * .class
     */
    public static final String SUFFIX_CLASS = ".class";
    /**
     * dtd
     */
    public static final String DTD = "dtd";

    /**
     * 空值
     */
    public static final Pattern PATTERN_EMPTY = Pattern.compile("\\s+");
    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     */
    public static final Pattern DECIMALS = Pattern.compile(
            "(-?\\d*([fdFD])*)|(-?\\d*\\.\\d*([fdFD])*)|(-?\\d*\\.\\d*e\\d*)|(-?\\d*e\\d*)");
    /**
     * 验证日期（年月日）
     */
    public static final Pattern DATE = Pattern.compile("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}");
    /**
     * 地址
     */
    public static final Pattern ADDRESS = Pattern.compile("([1-9]|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3}");

    /**
     * 英文字母 、数字和下划线
     */
    public static final String GENERAL = "^\\w+$";
    /**
     * 数字
     */
    public static final String NUMBERS = "\\d+";
    /**
     * 字母
     */
    public static final String WORD = "[a-zA-Z]+";
    /**
     * 单个中文汉字
     */
    public static final String CHINESE_V2 = "[\u4E00-\u9FFF]";
    /**
     * 中文汉字
     */
    public static final String CHINESES = CHINESE_V2 + "+";
    /**
     * 分组
     */
    public static final String GROUP_VAR = "\\$(\\d+)";
    /**
     * IP v4<br>
     * 采用分组方式便于解析地址的每一个段
     */
    /**
     * String IPV4 = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
     */
    public static final String IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$";
    /**
     * IP v6
     */
    public static final String IPV6 = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))";
    /**
     * 货币
     */
    public static final String MONEY = "^(\\d+(?:\\.\\d+)?)$";
    /**
     * 邮件，符合RFC 5322规范，正则来自：http://emailregex.com/
     * What is the maximum length of a valid email address? https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754
     * 注意email 要宽松一点。比如 jetz.chong@hutool.cn、jetz-chong@ hutool.cn、jetz_chong@hutool.cn、dazhi.duan@hutool.cn 宽松一点把，都算是正常的邮箱
     */
    public static final String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
    /**
     * 移动电话
     */
    public static final String MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}";
    /**
     * 中国香港移动电话
     * eg: 中国香港： +852 5100 4810， 三位区域码+10位数字, 中国香港手机号码8位数
     * eg: 中国大陆： +86  180 4953 1399，2位区域码标示+13位数字
     * 中国大陆 +86 Mainland China
     * 中国香港 +852 Hong Kong
     * 中国澳门 +853 Macao
     * 中国台湾 +886 Taiwan
     */
    public static final String MOBILE_HK = "(?:0|852|\\+852)?\\d{8}";
    /**
     * 中国台湾移动电话
     * eg: 中国台湾： +886 09 60 000000， 三位区域码+号码以数字09开头 + 8位数字, 中国台湾手机号码10位数
     * 中国台湾 +886 Taiwan 国际域名缩写：TW
     */
    public static final String MOBILE_TW = "(?:0|886|\\+886)?(?:|-)09\\d{8}";
    /**
     * 中国澳门移动电话
     * eg: 中国台湾： +853 68 00000， 三位区域码 +号码以数字6开头 + 7位数字, 中国台湾手机号码8位数
     * 中国澳门 +853 Macao 国际域名缩写：MO
     */
    public static final String MOBILE_MO = "(?:0|853|\\+853)?(?:|-)6\\d{7}";
    /**
     * 座机号码<br>
     * pr#387@Gitee
     */
    public static final String TEL = "(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})";
    /**
     * 座机号码+400+800电话
     *
     * @see <a href="https://baike.baidu.com/item/800">800</a>
     */
    public static final String TEL_400_800 = "0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}";
    /**
     * 18位身份证号码
     */
    public static final String CITIZEN_ID = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)";
    /**
     * 邮编，兼容港澳台
     */
    public static final String ZIP_CODE = "^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$";
    /**
     * 生日
     */
    public static final String BIRTHDAY = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$";
    /**
     * URI<br>
     * 定义见：https://www.ietf.org/rfc/rfc3986.html#appendix-B
     */
    public static final String URI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    /**
     * URL
     */
    public static final String URL = "[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
    /**
     * Http URL（来自：http://urlregex.com/）<br>
     * 此正则同时支持FTP、File等协议的URL
     */
    public static final String URL_HTTP = "(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
    /**
     * 中文字、英文字母、数字和下划线
     */
    public static final String GENERAL_WITH_CHINESE = "^[\u4E00-\u9FFF\\w]+$";
    /**
     * UUID
     */
    public static final String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    /**
     * 不带横线的UUID
     */
    public static final String UUID_SIMPLE = "^[0-9a-fA-F]{32}$";
    /**
     * MAC地址正则
     */
    public static final String MAC_ADDRESS = "((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER";
    /**
     * 16进制字符串
     */
    public static final String HEX = "^[a-fA-F0-9]+$";
    /**
     * 时间正则
     */
    public static final String TIME = "\\d{1,2}:\\d{1,2}(:\\d{1,2})?";
    /**
     * 中国车牌号码（兼容新能源车牌）
     */
    public static final String PLATE_NUMBER =
            //https://gitee.com/loolly/hutool/issues/I1B77H?from=project-issue
            "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|" +
                    //https://gitee.com/loolly/hutool/issues/I1BJHE?from=project-issue
                    "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|" +
                    "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";

    /**
     * 社会统一信用代码
     * <pre>
     * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
     * 第二部分：机构类别代码1位 (数字或大写英文字母)
     * 第三部分：登记管理机关行政区划码6位 (数字)
     * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
     * 第五部分：校验码1位 (数字或大写英文字母)
     * </pre>
     */
    public static final String CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    /**
     * 车架号
     * 别名：车辆识别代号 车辆识别码
     * eg:LDC613P23A1305189
     * eg:LSJA24U62JG269225
     * 十七位码、车架号
     * 车辆的唯一标示
     */
    public static final String CAR_VIN = "^[A-Za-z0-9]{17}$";
    /**
     * 驾驶证  别名：驾驶证档案编号、行驶证编号
     * eg:430101758218
     * 12位数字字符串
     * 仅限：中国驾驶证档案编号
     */
    public static final String CAR_DRIVING_LICENCE = "^[0-9]{12}$";


    /**
     *
     */
    public static final String SYMBOL_EMPTY_STRING = "";

    /**
     * 字符串常量：Windows 换行 {@code "\r\n"} <br>
     * 解释：该字符串常用于表示 Windows 系统下的文本换行
     */
    public static final String CRLF = "\r\n";

    /**
     * <
     */
    public static final char LESS_THAN = '<';
    /**
     * >
     */
    public static final char GREATER_THAN = '>';

    /**
     * '1'
     */
    public static final char ONE_CHAR = '1';

    /**
     * '2'
     */
    public static final char TWE_CHAR = '2';
    /**
     * 3
     */
    public static final char THREE_CHAR = '3';

    /**
     * '9'
     */
    public static final char NIGHT_CHAR = '9';
    /**
     * "0"
     */
    public static final String ZERO_STR = "0";
    /**
     * '1'
     */
    public static final String ONE_STR = "1";

    /**
     * '2'
     */
    public static final String TWE_STR = "2";
    /**
     * 3
     */
    public static final String THREE_STR = "3";
    /**
     * 4
     */
    public static final String FOUR_STR = "4";
    /**
     * 5
     */
    public static final String FIVE_STR = "5";
    /**
     * 6
     */
    public static final String SIX_STR = "6";
    /**
     * 7
     */
    public static final String SEVEN_STR = "7";
    /**
     * 8
     */
    public static final String EIGHT_STR = "8";
    /**
     * 9
     */
    public static final String NIGHT_STR = "9";
    /**
     * "on"
     */
    public static final String ON = "on";
    /**
     * "yes"
     */
    public static final String YES = "yes";
    /**
     * "no"
     */
    public static final String NO = "no";
    /**
     * "off"
     */
    public static final String OFF = "off";
    /**
     * ...
     */
    public static final String PATH_MORE = "...";
}
