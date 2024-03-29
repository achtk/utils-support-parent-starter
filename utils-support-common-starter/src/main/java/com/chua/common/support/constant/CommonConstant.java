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
public interface CommonConstant extends NameConstant{
   String WILDCARD = "*";
   int INVALID_EXITVALUE = 0xdeadbeef;

   int MAX_HTTP_CONTENT_LENGTH = 1024 * 1024 * 10;
   int MAX_CACHED_BUILDER_SIZE = 8 * 1024;
   int MAX_IDLE_BUILDERS = NumberConstant.EIGHT;
    /**
     * -1
     */
   String SYMBOL_EOF = "-1";
    /**
     * |
     */
   String SYMBOL_PIPE = "|";
    /**
     * |
     */
   char SYMBOL_PIPE_CHAR = '|';
    /**
     * @
     */
   String SYMBOL_AT = "@";
    /**
     * !
     */
   String SYMBOL_EXCLAMATION_MARK = "!";
    /**
     * @
     */
   char SYMBOL_AT_CHAR = '@';
    /**
     * 默认缓存大小 8192
     */
   int DEFAULT_BUFFER_SIZE = 2 << 12;
    /**
     * 默认中等缓存大小 16384
     */
   int DEFAULT_MIDDLE_BUFFER_SIZE = 2 << 13;
    /**
     * 默认大缓存大小 32768
     */
   int DEFAULT_LARGE_BUFFER_SIZE = 2 << 14;

    /**
     * 数据流末尾
     */
   int EOF = -1;
   ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
   DecimalFormat FORMAT = new DecimalFormat("###.000");
   String[] EMPTY_ARRAY = new String[0];
   Method[] EMPTY_METHOD_ARRAY = new Method[0];
   String[] EMPTY_STRING_ARRAY = EMPTY_ARRAY;
   Integer[] EMPTY_INTEGER = new Integer[0];
   byte[] EMPTY_BYTE = new byte[0];
   Object[] EMPTY_OBJECT = new Object[0];
   Object[] EMPTY_OBJECT_ARRAY = EMPTY_OBJECT;
   Class<?>[] EMPTY_CLASS = new Class<?>[0];
    /**
     * 字母开头
     */
   Pattern START_WITH_WORD_REGEX = Pattern.compile("^[A-Za-z].*");
    /**
     * 纯数字
     */
   Pattern NUMERIC_REGEX = Pattern.compile("[0-9]+");
    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
   int PAD_LIMIT = 8192;
   String EMPTY = "";
   int TWE = NumberConstant.TWE;
    /**
     * os名称
     */
   String OS_NAME = System.getProperty("os.name");
    /**
     * {}
     */
   String EMPTY_JSON = "{}";
    /**
     * ""
     */
   String SYMBOL_EMPTY = "";
    /**
     * [
     */
   String SYMBOL_LEFT_SQUARE_BRACKET = "[";
    /**
     * [
     */
   char SYMBOL_LEFT_SQUARE_BRACKET_CHAR = '[';
    /**
     * ]
     */
   char SYMBOL_RIGHT_SQUARE_BRACKET_CHAR = ']';
    /**
     * <
     */
   String SYMBOL_LEFT_TRIANGLE_BRACKET = "<";
    /**
     * ]
     */
   String SYMBOL_RIGHT_SQUARE_BRACKET = "]";
    /**
     * ]:
     */
   String SYMBOL_RIGHT_SQUARE_BRACKET_COLON = "]:";
    /**
     * $=
     */
   String SYMBOL_DOLLAR_EQUALS = "$=";
    /**
     * $
     */
   String SYMBOL_DOLLAR = "$";
    /**
     * $
     */
   char SYMBOL_DOLLAR_CHAR = '$';
    /**
     * $
     */
   String SYMBOL_XPATH = "$.";
    /**
     * " "
     */
   String SYMBOL_BLANK = " ";
    /**
     * '
     */
   String SYMBOL_SINGLE_QUOTATION_MARK = "'";
    /**
     * " "
     */
   String SYMBOL_SPACE = SYMBOL_BLANK;
    /**
     * .
     */
   String SYMBOL_DOT = ".";
    /**
     * AS
     */
   String SYMBOL_AS = " AS ";
    /**
     * ..
     */
   String SYMBOL_DOUBLE_DOT = "..";
    /**
     * .*
     */
   String SYMBOL_DOT_ASTERISK = ".*";
    /**
     * //
     */
   String SYMBOL_DOUBLE_LEFT_SLASH = "//";
    /**
     * /
     */
   String SYMBOL_LEFT_SLASH = "/";
    /**
     * #/
     */
   String SYMBOL_LEFT_HASH_SLASH = "#/";
    /**
     * "
     */
   String SYMBOL_QUOTE = "\"";
    /**
     * \n
     */
   String SYMBOL_NEWLINE = "\n";
    /**
     * #{
     */
   String SYMBOL_HASH_LEFT_BRACE = "#{";
    /**
     * ${
     */
   String SYMBOL_DOLLAR_LEFT_BRACE = "${";
    /**
     * "
     */
   char SYMBOL_QUOTE_CHAR = '"';
    /**
     * #
     */
   String SYMBOL_HASH = "#";
    /**
     * #
     */
   char SYMBOL_HASH_CHAR = '#';
    /**
     * \
     */
   String SYMBOL_RIGHT_SLASH = "\\";
    /**
     * \\'
     */
   char SYMBOL_RIGHT_SLASH_CHAR = '\\';
    /**
     * &&
     */
   String SYMBOL_DOUBLE_AND = "&&";
    /**
     * ||
     */
   String SYMBOL_DOUBLE_PIPE = "||";
    /**
     * \'
     */
   char SYMBOL_RIGHT_ONE_SLASH_CHAR = '\'';
    /**
     * ,
     */
   String SYMBOL_COMMA = ",";
    /**
     * ,
     */
   char SYMBOL_COMMA_CHAR = ',';
    /**
     * ;
     */
   String SYMBOL_SEMICOLON = ";";
    /**
     * ;
     */
   char SYMBOL_SEMICOLON_CHAR = ';';
    /**
     * :
     */
   String SYMBOL_COLON = ":";
    /**
     * ^
     */
   String SYMBOL_EXPONENT = "^";
    /**
     * -
     */
   String SYMBOL_MINUS = "-";
    /**
     * -
     */
   char SYMBOL_MINUS_CHAR = '-';
    /**
     * +
     */
   String SYMBOL_PLUS = "+";
    /**
     * +
     */
   char SYMBOL_PLUS_CHAR = '+';
    /**
     * \r
     */
   char SYMBOL_R_CHAR = '\r';
    /**
     * \n
     */
   char SYMBOL_N_CHAR = '\n';
    /**
     * _
     */
   String SYMBOL_UNDERLINE = "_";
    /**
     * _
     */
   String SYMBOL_UNDERSCORE = SYMBOL_UNDERLINE;
    /**
     * -
     */
   String SYMBOL_MINS = "-";
    /**
     * =
     */
   String SYMBOL_EQUALS = "=";
    /**
     * =
     */
   char SYMBOL_EQUALS_CHAR = '=';
    /**
     * >
     */
   String SYMBOL_RIGHT_CHEV = ">";
    /**
     * <
     */
   String SYMBOL_LEFT_CHEV = "<";
    /**
     * %
     */
   String SYMBOL_PERCENT = "%";
    /**
     * &
     */
   char SYMBOL_AND_CHAR = '&';
    /**
     * &
     */
   String SYMBOL_AND = "&";
    /**
     * \n
     */
   String SYMBOL_N = "\n";
    /**
     * true
     */
   String TRUE = "true";
    /**
     * false
     */
   String FALSE = "false";
    /**
     * \t
     */
   char SYMBOL_T_CHAR = '\t';
    /**
     * \0
     */
   char SYMBOL_NULL_CHAR = '\0';
    /**
     * ' '
     */
   char SYMBOL_BLANK_CHAR = ' ';
    /**
     * )
     */
   String SYMBOL_RIGHT_BRACKETS = ")";
    /**
     * )
     */
   String SYMBOL_RIGHT_BRACKET = SYMBOL_RIGHT_BRACKETS;
    /**
     * (
     */
   String SYMBOL_LEFT_BRACKETS = "(";
    /**
     * (
     */
   char SYMBOL_LEFT_BRACKETS_CHAR = '(';
    /**
     * )
     */
   char SYMBOL_RIGHT_BRACKETS_CHAR = ')';
    /**
     * (
     */
   String SYMBOL_LEFT_BRACKET = SYMBOL_LEFT_BRACKETS;
    /**
     * *
     */
   String SYMBOL_ASTERISK = "*";
    /**
     * **
     */
   String SYMBOL_ASTERISK_ANY = "**";
    /**
     * ?
     */
   String SYMBOL_QUESTION = "?";
    /**
     * {
     */
   String SYMBOL_LEFT_BIG_PARENTHESES = "{";
    /**
     * }
     */
   String SYMBOL_RIGHT_BIG_PARENTHESES = "}";

    /**
     * }
     */
   String SYMBOL_RIGHT_BRACE = SYMBOL_RIGHT_BIG_PARENTHESES;

    /**
     * }
     */
   String SYMBOL_LEFT_BRACE = SYMBOL_LEFT_BIG_PARENTHESES;

    /**
     * ~
     */
   String SYMBOL_WAVY_LINE = "~";
    //*****************************************char******************************************/
    /**
     * .
     */
   char SYMBOL_DOT_CHAR = '.';
    /**
     * /
     */
   char SYMBOL_LEFT_SLASH_CHAR = '/';
    /**
     * '?'
     */
   char SYMBOL_QUESTION_CHAR = '?';
    /**
     * {
     */
   char SYMBOL_LEFT_BIG_PARANTHESES_CHAR = '{';
    /**
     * }
     */
   char SYMBOL_RIGHT_BIG_PARANTHESES_CHAR = '}';
    /**
     * :
     */
   char SYMBOL_COLON_CHAR = ':';
    /**
     * ::
     */
   String SYMBOL_DOUBLE_COLON = "::";
    /**
     * ~
     */
   char SYMBOL_WAVY_LINE_CHAR = '~';
    /**
     * ~
     */
   char SYMBOL_DASH_CHAR = '_';
    /**
     * *
     */
   char SYMBOL_ASTERISK_CHAR = '*';
    /**
     * '0'
     */
   char CHARACTER_0 = '0';
    /**
     * '9'
     */
   char CHARACTER_9 = '9';
    /**
     * 'A'
     */
   char LETTER_UPPERCASE_A = 'A';
    /**
     * 'X'
     */
   char LETTER_UPPERCASE_X = 'X';
    /**
     * 'C'
     */
   char LETTER_UPPERCASE_C = 'C';
    /**
     * V
     */
   char LETTER_UPPERCASE_V = 'V';
    /**
     * D
     */
   char LETTER_UPPERCASE_D = 'D';
    /**
     * J
     */
   char LETTER_UPPERCASE_J = 'J';
    /**
     * O
     */
   char LETTER_UPPERCASE_O = 'O';
    /**
     * N
     */
   char LETTER_UPPERCASE_N = 'N';
    /**
     * I
     */
   char LETTER_UPPERCASE_I = 'I';
    /**
     * W
     */
   char LETTER_UPPERCASE_W = 'W';
    /**
     * Y
     */
   char LETTER_UPPERCASE_Y = 'Y';
    /**
     * 'L'
     */
   char LETTER_UPPERCASE_L = 'L';
    /**
     * T
     */
   char LETTER_UPPERCASE_T = 'T';
    /**
     * P
     */
   char LETTER_UPPERCASE_P = 'P';
    /**
     * F
     */
   char LETTER_UPPERCASE_F = 'F';
    /**
     * M
     */
   char LETTER_UPPERCASE_M = 'M';
    /**
     * B
     */
   char LETTER_UPPERCASE_B = 'B';
    /**
     * G
     */
   char LETTER_UPPERCASE_G = 'G';
    /**
     * Q
     */
   char LETTER_UPPERCASE_Q = 'Q';
    /**
     * 'Z'
     */
   char LETTER_UPPERCASE_Z = 'Z';
    /**
     * 'k'
     */
   char LETTER_LOWERCASE_K = 'k';
    /**
     * 'i'
     */
   char LETTER_LOWERCASE_I = 'i';
    /**
     * 'o'
     */
   char LETTER_LOWERCASE_O = 'o';
    /**
     * 'u'
     */
   char LETTER_LOWERCASE_U = 'u';
    /**
     * 'a'
     */
   char LETTER_LOWERCASE_A = 'a';
    /**
     * 'x'
     */
   char LETTER_LOWERCASE_X = 'x';
    /**
     * 'f'
     */
   char LETTER_LOWERCASE_F = 'f';
    /**
     * n
     */
   char LETTER_LOWERCASE_N = 'n';
    /**
     * t
     */
   char LETTER_LOWERCASE_T = 't';
    /**
     * r
     */
   char LETTER_LOWERCASE_R = 'r';
    /**
     * e
     */
   char LETTER_LOWERCASE_E = 'e';
    /**
     * 'z'
     */
   char LETTER_LOWERCASE_Z = 'z';
    /**
     * {}
     */
   Properties EMPTY_PROPERTIES = new Properties();

    /**
     * -1
     */
   int INDEX_NOT_FOUND = -1;
    /**
     * path.separator
     */
   String PATH_SEPARATOR = "path.separator";
    /**
     * java.class.path
     */
   String JAVA_CLASS_PATH = "java.class.path";
    /**
     * -------------------------------------格式化-------------------------------------
     */
   DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.000");
    /**
     * -------------------------------------资源文件-------------------------------------
     */
   String RESOURCE_MESSAGE = "language/message";
    //-------------------------------------基础常量-------------------------------------*/
    /**
     * Used to build output as Hex
     */
   char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
   char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * ".*\\.jar(\\!.*|$)"
     */
   String JAR_PATTERN = ".*\\.jar(\\!.*|$)";

    /**
     * A
     */
   char LETTER_A = 'A';
    /**
     * F
     */
   char LETTER_F = 'F';
    /**
     * Z
     */
   char LETTER_Z = 'Z';
    /**
     * 0x
     */
   String HEX_16 = "0x";
    /**
     * 0X
     */
   String HEX_16_UPPER = "0X";


    /**
     * 系统语言环境，默认为中文zh
     */
   String LANGUAGE = "zh";
    /**
     * 系统国家环境，默认为中国CN
     */
   String COUNTRY = "CN";
    /**
     * /jre
     */
   String PATH_JRE = "/jre";
    /**
     * sun
     */
   String SUN = "sun";
    /**
     * 0
     */
   String ZERO = "0";
    /**
     * any
     */
   String ANY = "any";
    /**
     * any
     */
   String ANY_PROTOCOL = "any:";
    /**
     * private
     */
   String PRIVATE = "private";
    /**
     * protect
     */
   String PROTECT = "protect";
    /**
     * public
     */
   String PUBLIC = "public";
    /**
     * $javassist
     */
   String JAVASSIST = "$javassist";
    /**
     * javassist
     */
   String NAMED_JAVASSIST = "javassist";
    /**
     * jdk
     */
   String NAMED_JDK = "jdk";
    /**
     * window
     */
   String WINDOW = "window";
    /**
     * linux
     */
   String LINUX = "linux";
    /**
     * class
     */
   String CLASS = "class";
    /**
     * !=
     */
   String NO_EQUALS = "!=";
    /**
     * *=
     */
   String SYMBOL_ASTERISK_EQUALS = "*=";
    /**
     * ~=
     */
   String SYMBOL_TILDE_EQUALS = "~=";
    /**
     * ^=
     */
   String XOR = "^=";
    /**
     * duplicate
     */
   CharSequence DUPLICATE = "duplicate";
    /**
     * GMT
     */
   String GMT = "GMT";
    /**
     * 映射
     */
   String MAPPER = "->";
    /**
     * Pseudo URL prefix for loading from the class path: "leaf:".
     */
   String LEAF_URL_PREFIX = "leaf:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
   String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
   String CLASSPATH_URL_ALL_PREFIX = "classpath*:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
   String FILE_SYSTEM_URL_PREFIX = "filesystem:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
   String FILE_SYSTEM_URL_ALL_PREFIX = "filesystem*:";
    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
   String XPATH_URL_PREFIX = "xpath:";
    /**
     * Pseudo URL prefix for loading from the class path: "subclass:".
     */
   String SUBCLASS_URL_PREFIX = "subclass:";
    /**
     * Pseudo URL prefix for loading from the class path: "class:".
     */
   String CLASS_URL_PREFIX = "class:";
    /**
     * compress:
     */
   String COMPRESS_URL_PREFIX = "compress:";
    /**
     * URL prefix for loading from the file system: "file:".
     */
   String FILE_URL_PREFIX = "file:";
    /**
     * URL prefix for loading from the file system: "file:".
     */
   String FTP_URL_PREFIX = "ftp:";
    /**
     * URL prefix for loading from a jar file: "jar:".
     */
   String JAR_URL_PREFIX = "jar:";
    /**
     * URL prefix for loading from a jar file: "wsjar:".
     */
   String WS_JAR_URL_PREFIX = "wsjar:";
    /**
     * URL prefix for loading from a war file on Tomcat: "war:".
     */
   String WAR_URL_PREFIX = "war:";
    /**
     * URL protocol for a file in the file system: "file".
     */
   String URL_PROTOCOL_FILE = FILE;
    /**
     * URL protocol for an entry from a jar file: "jar".
     */
   String URL_PROTOCOL_JAR = JAR;
    /**
     * URL protocol for an entry from a war file: "war".
     */
   String URL_PROTOCOL_WAR = WAR;
    /**
     * URL protocol for an entry from a zip file: "zip".
     */
   String URL_PROTOCOL_ZIP = ZIP;
    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar".
     */
   String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip".
     */
   String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL protocol for a JBoss file system resource: "vfsfile".
     */
   String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL protocol for a general JBoss VFS resource: "vfs".
     */
   String URL_PROTOCOL_VFS = "vfs";
    /**
     * File extension for a regular jar file: ".war!".
     */
   String WAR_FILE_EXTENSION_IN = ".war!";
    /**
     * File extension for a regular jar file: ".war".
     */
   String WAR_FILE_EXTENSION = ".war";
    /**
     * File extension for a regular jar file: ".jar".
     */
   String JAR_FILE_EXTENSION = ".jar";
    /**
     * File extension for a regular jar file: ".jar!".
     */
   String JAR_FILE_EXTENSION_IN = ".jar!";
    /**
     * Separator between JAR URL and file path within the JAR: "!/".
     */
   String JAR_URL_SEPARATOR = "!/";
    /**
     * Special separator between WAR URL and jar part on Tomcat.
     */
   String WAR_URL_SEPARATOR = "*/";


   String PIC_SOURCE = "png,jpeg,jpg,bmp,tiff,tif";

   String SYMBOL_EMPTY_ARRAY = "[]";
   String SYMBOL_EMPTY_MAP = "{}";
   char LETTER_NIGHT = CHARACTER_9;
   char LETTER_ZERO = CHARACTER_0;
   String UNKNOWN = "unknown";
    /**
     * "already exists"
     */
   CharSequence EXIST_SIGN = "already exists";
   String WHITESPACE = " \n\r\f\t";
   String PREVIEW = "preview";

   String DOWNLOAD = "download";
   String XZ = ".xz";
   String GZ = ".gz";
   String EMPTY_STRING = "";
   String CUT = "[,;]{1}";
   CharSequence POM = "pom.xml";
    /**
     * ' '
     */
   char SYMBOL_SPACE_CHAR = ' ';



    /**
     * .class
     */
   String SUFFIX_CLASS = ".class";
    /**
     * dtd
     */
   String DTD = "dtd";

    /**
     * 空值
     */
   Pattern PATTERN_EMPTY = Pattern.compile("\\s+");
    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     */
   Pattern DECIMALS = Pattern.compile(
            "(-?\\d*([fdFD])*)|(-?\\d*\\.\\d*([fdFD])*)|(-?\\d*\\.\\d*e\\d*)|(-?\\d*e\\d*)");
    /**
     * 验证日期（年月日）
     */
   Pattern DATE = Pattern.compile("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}");
    /**
     * 地址
     */
   Pattern ADDRESS = Pattern.compile("([1-9]|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3}");

    /**
     * 英文字母 、数字和下划线
     */
   String GENERAL = "^\\w+$";
    /**
     * 数字
     */
   String NUMBERS = "\\d+";
    /**
     * 字母
     */
   String WORD = "[a-zA-Z]+";
    /**
     * 单个中文汉字
     */
   String CHINESE_V2 = "[\u4E00-\u9FFF]";
    /**
     * 中文汉字
     */
   String CHINESES = CHINESE_V2 + "+";
    /**
     * 分组
     */
   String GROUP_VAR = "\\$(\\d+)";
    /**
     * IP v4<br>
     * 采用分组方式便于解析地址的每一个段
     */
    /**
     * String IPV4 = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
     */
   String IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)$";
    /**
     * IP v6
     */
   String IPV6 = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))";
    /**
     * 货币
     */
   String MONEY = "^(\\d+(?:\\.\\d+)?)$";
    /**
     * 邮件，符合RFC 5322规范，正则来自：http://emailregex.com/
     * What is the maximum length of a valid email address? https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754
     * 注意email 要宽松一点。比如 jetz.chong@hutool.cn、jetz-chong@ hutool.cn、jetz_chong@hutool.cn、dazhi.duan@hutool.cn 宽松一点把，都算是正常的邮箱
     */
   String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
    /**
     * 移动电话
     */
   String MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}";
    /**
     * 中国香港移动电话
     * eg: 中国香港： +852 5100 4810， 三位区域码+10位数字, 中国香港手机号码8位数
     * eg: 中国大陆： +86  180 4953 1399，2位区域码标示+13位数字
     * 中国大陆 +86 Mainland China
     * 中国香港 +852 Hong Kong
     * 中国澳门 +853 Macao
     * 中国台湾 +886 Taiwan
     */
   String MOBILE_HK = "(?:0|852|\\+852)?\\d{8}";
    /**
     * 中国台湾移动电话
     * eg: 中国台湾： +886 09 60 000000， 三位区域码+号码以数字09开头 + 8位数字, 中国台湾手机号码10位数
     * 中国台湾 +886 Taiwan 国际域名缩写：TW
     */
   String MOBILE_TW = "(?:0|886|\\+886)?(?:|-)09\\d{8}";
    /**
     * 中国澳门移动电话
     * eg: 中国台湾： +853 68 00000， 三位区域码 +号码以数字6开头 + 7位数字, 中国台湾手机号码8位数
     * 中国澳门 +853 Macao 国际域名缩写：MO
     */
   String MOBILE_MO = "(?:0|853|\\+853)?(?:|-)6\\d{7}";
    /**
     * 座机号码<br>
     * pr#387@Gitee
     */
   String TEL = "(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})";
    /**
     * 座机号码+400+800电话
     *
     * @see <a href="https://baike.baidu.com/item/800">800</a>
     */
   String TEL_400_800 = "0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}";
    /**
     * 18位身份证号码
     */
   String CITIZEN_ID = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)";
    /**
     * 邮编，兼容港澳台
     */
   String ZIP_CODE = "^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$";
    /**
     * 生日
     */
   String BIRTHDAY = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$";
    /**
     * URI<br>
     * 定义见：https://www.ietf.org/rfc/rfc3986.html#appendix-B
     */
   String URI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    /**
     * URL
     */
   String URL = "[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
    /**
     * Http URL（来自：http://urlregex.com/）<br>
     * 此正则同时支持FTP、File等协议的URL
     */
   String URL_HTTP = "(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]";
    /**
     * 中文字、英文字母、数字和下划线
     */
   String GENERAL_WITH_CHINESE = "^[\u4E00-\u9FFF\\w]+$";
    /**
     * UUID
     */
   String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    /**
     * 不带横线的UUID
     */
   String UUID_SIMPLE = "^[0-9a-fA-F]{32}$";
    /**
     * MAC地址正则
     */
   String MAC_ADDRESS = "((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER";
    /**
     * 16进制字符串
     */
   String HEX = "^[a-fA-F0-9]+$";
    /**
     * 时间正则
     */
   String TIME = "\\d{1,2}:\\d{1,2}(:\\d{1,2})?";
    /**
     * 中国车牌号码（兼容新能源车牌）
     */
   String PLATE_NUMBER =
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
   String CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    /**
     * 车架号
     * 别名：车辆识别代号 车辆识别码
     * eg:LDC613P23A1305189
     * eg:LSJA24U62JG269225
     * 十七位码、车架号
     * 车辆的唯一标示
     */
   String CAR_VIN = "^[A-Za-z0-9]{17}$";
    /**
     * 驾驶证  别名：驾驶证档案编号、行驶证编号
     * eg:430101758218
     * 12位数字字符串
     * 仅限：中国驾驶证档案编号
     */
   String CAR_DRIVING_LICENCE = "^[0-9]{12}$";


    /**
     *
     */
   String SYMBOL_EMPTY_STRING = "";

    /**
     * 字符串常量：Windows 换行 {@code "\r\n"} <br>
     * 解释：该字符串常用于表示 Windows 系统下的文本换行
     */
   String CRLF = "\r\n";

    /**
     * <
     */
   char LESS_THAN = '<';
    /**
     * >
     */
   char GREATER_THAN = '>';

    /**
     * '1'
     */
   char ONE_CHAR = '1';

    /**
     * '2'
     */
   char TWE_CHAR = '2';
    /**
     * 3
     */
   char THREE_CHAR = '3';

    /**
     * '9'
     */
   char NIGHT_CHAR = '9';
    /**
     * "0"
     */
   String ZERO_STR = "0";
    /**
     * '1'
     */
   String ONE_STR = "1";

    /**
     * '2'
     */
   String TWE_STR = "2";
    /**
     * 3
     */
   String THREE_STR = "3";
    /**
     * 4
     */
   String FOUR_STR = "4";
    /**
     * 5
     */
   String FIVE_STR = "5";
    /**
     * 6
     */
   String SIX_STR = "6";
    /**
     * 7
     */
   String SEVEN_STR = "7";
    /**
     * 8
     */
   String EIGHT_STR = "8";
    /**
     * 9
     */
   String NIGHT_STR = "9";
    /**
     * "on"
     */
   String ON = "on";
    /**
     * "yes"
     */
   String YES = "yes";
    /**
     * "no"
     */
   String NO = "no";
    /**
     * "off"
     */
   String OFF = "off";
    /**
     * ...
     */
   String PATH_MORE = "...";
}
