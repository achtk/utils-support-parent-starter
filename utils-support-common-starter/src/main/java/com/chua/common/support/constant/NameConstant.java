package com.chua.common.support.constant;

import com.chua.common.support.protocol.server.Constant;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT;

/**
 * 命名
 *
 * @author CH
 */
public interface NameConstant extends Constant {
    /**
     * http架构错误
     */
    String HTTP_SCHEMA_ERROR = "http和https参数错误";
    /**
     * cglib
     */
    CharSequence CGLIB$$ = "CGLIB$$";
    /**
     * CDATA
     */
    String CDATA = "CDATA";
    /**
     * with
     */
    String WITH = "with";
    /**
     * as
     */
    String AS = "as";
    /**
     * length
     */
    String LENGTH = "length";


    String FTP = "ftp";
    /**
     * GET
     */
    String GET = "GET";
    /**
     * POST
     */
    String POST = "POST";
    /**
     * DELETE
     */
    String DELETE = "DELETE";
    /**
     * PUT
     */
    String PUT = "PUT";
    /**
     * set方法
     */
    String METHOD_SETTER = "set";
    /**
     * get方法
     */
    String METHOD_GETTER = "get";


    /**
     * toString
     */
    String METHOD_TO_STRING = "toString";
    /**
     * equals
     */
    String METHOD_EQUALS = "equals";
    /**
     * hashCode
     */
    String METHOD_HASH_CODE = "hashCode";

    /**
     * source
     */
    String SOURCE = "source";
    String HTTP = "http";
    String PATH = "path";
    String FILE_FILTERS = "fileFilters";
    String JPG = "jpg";
    String JPEG = "jpeg";

    /**
     * default
     */
    String DEFAULT = "default";

    /**
     * default
     */
    String SYSTEM = "system";
    /**
     * null
     */
    String NULL = "null";
    /**
     * now
     */
    String NOW = "now";
    /**
     * null
     */
    String NULL_CHAR = "'null'";
    /**
     * 处理占位符对象属性
     */
    String SYSTEM_PLACEHOLDER_PROP = "system.prop.placeholder";
    /**
     * 处理文件优先级
     */
    String SYSTEM_PRIORITY_PROP = "system.prop.priority";
    /**
     * spi默认配置
     */
    String SPI_CONFIG_DEFAULT = "spi-config-default";
    /**
     * spi配置
     */
    String SPI_CONFIG = "spi-config.json";
    /**
     * resource默认配置
     */
    String RESOURCE_CONFIG_DEFAULT = "resource-config-default.json";
    /**
     * host
     */
    String CONFIG_FIELD_HOST = "host";
    /**
     * port
     */
    String CONFIG_FIELD_PORT = "port";
    /**
     * password
     */
    String CONFIG_FIELD_PASSWORD = "password";
    /**
     * username
     */
    String CONFIG_FIELD_USERNAME = "username";
    /**
     * 最大连接数
     */
    String CONFIG_FIELD_MAX_CONNECTION = "maxConnection";
    /**
     * 连接超时
     */
    String CONFIG_FIELD_CONNECTION_TIMEOUT = "connectionTimeout";
    /**
     * 读取超时
     */
    String CONFIG_FIELD_READ_TIMEOUT = "readTimeout";
    /**
     * 写入超时
     */
    String CONFIG_FIELD_WRITE_TIMEOUT = "writeTimeout";
    /**
     * 路径
     */
    String CONFIG_FIELD_PATH = "path";
    /**
     * 超时时间
     */
    String CONFIG_FIELD_SESSION_TIMEOUT = "sessionTimeout";
    /**
     * 重试
     */
    String CONFIG_FIELD_RETRY = "retry";
    /**
     * jar
     */
    String JAR = "jar";

    /**
     * file
     */
    String FILE = "file";
    /**
     * file
     */
    String WAR = "war";
    /**
     * zip
     */
    String ZIP = "zip";
    /**
     * gzip
     */
    String GZIP = "gzip";

    /**
     * wrapper 内部参数相关
     */
    String SYMBOL_WRAPPER_PARAM = "MPGENVAL";
    String SYMBOL_WRAPPER_PARAM_MIDDLE = ".paramNameValuePairs" + SYMBOL_DOT;

    /**
     * master
     */

    String MASTER = "master";
    /**
     * order by
     */
    CharSequence SYMBOL_ORDER_BY = " ORDER BY ";
    /**
     * limit
     */
    CharSequence SYMBOL_LIMIT = " limit ";
    /**
     * ew
     */
    String SYMBOL_WRAPPER = "ew";
    /**
     * select
     */
    CharSequence SYMBOL_SELECT = " SELECT ";
    /**
     * INSERT
     */
    CharSequence SYMBOL_INSERT = " INSERT INTO ";
    /**
     * update
     */
    CharSequence SYMBOL_UPDATE = " UPDATE ";
    /**
     * DELETE
     */
    CharSequence SYMBOL_DELETE = " DELETE FROM ";
    /**
     * create table
     */
    CharSequence SYMBOL_CREATE_TABLE = "CREATE TABLE";

    String SYMBOL_EXCEPTION = "Exception:";

    /**
     * where
     */
    String SYMBOL_WHERE = " WHERE ";
    /**
     * null
     */
    String SYMBOL_NULL_STRING = "null";
    /**
     * "java/lang/Object"
     */
    String OBJECT_NAME = "java/lang/Object";
    /**
     * "java/lang/String"
     */
    String STRING_NAME = "java/lang/String";
    /**
     * "java/lang/Class"
     */
    String CLASS_NAME = "java/lang/Class";
    /**
     * li
     */
    String LI_TAG = "li";
    /**
     * dt
     */
    String DT_TAG = "dt";
    /**
     * a
     */
    String A_TAG = "a";
    /**
     * "SITE"
     */
    String SITE = "SITE";
    /**
     * .gz
     */
    String SUFFIX_GZ = ".gz";
    /**
     * .z
     */
    String SUFFIX_Z = ".z";
    /**
     * .xml
     */
    String SUFFIX_XML = ".xml";
    /**
     * xml
     */
    String XML = "xml";
    /**
     * UPDATE
     */
    String UPDATE = "UPDATE";
    /**
     * INSERT
     */
    String INSERT = "INSERT";
    /**
     * union
     */
    String UNION = "union";
    /**
     * name
     */
    String NAME = "name";
    /**
     * pom.xml
     */
    String POM = "pom.xml";
    /**
     * .pom
     */
    String POM_SUFFIX = ".pom";
    /**
     * aes
     */
    String AES = "aes";
    /**
     * "elseif"
     */
    String ELSEIF = "elseif";
    /**
     * "else"
     */
    String ELSE = "else";
    /**
     * raw
     */
    String END = "end";
    /**
     * raw
     */
    String RAW = "raw";
    /**
     * "is"
     */
    String IS = "is";
    /**
     * MAVEN_TYPE_DEFINITION
     */
    String MAVEN_TYPE_DEFINITION = "com.chua.maven.support.depends.definition.MavenTypeDefinition";

    /**
     * 单表（增删改查）
     */
    String TPL_CRUD = "crud";

    /**
     * 树表（增删改查）
     */
    String TPL_TREE = "tree";

    /**
     * 主子表（增删改查）
     */
    String TPL_SUB = "sub";

    /**
     * 树编码字段
     */
    String TREE_CODE = "treeCode";

    /**
     * 树父编码字段
     */
    String TREE_PARENT_CODE = "treeParentCode";

    /**
     * 树名称字段
     */
    String TREE_NAME = "treeName";

    /**
     * 上级菜单ID字段
     */
    String PARENT_MENU_ID = "parentMenuId";

    /**
     * 上级菜单名称字段
     */
    String PARENT_MENU_NAME = "parentMenuName";

    /**
     * 数据库字符串类型
     */
    String[] COLUMNTYPE_STR = {"char", "varchar", "nvarchar", "varchar2"};

    /**
     * 数据库文本类型
     */
    String[] COLUMNTYPE_TEXT = {"tinytext", "text", "mediumtext", "longtext"};

    /**
     * 数据库时间类型
     */
    String[] COLUMNTYPE_TIME = {"datetime", "time", "date", "timestamp"};

    /**
     * 数据库数字类型
     */
     String[] COLUMNTYPE_NUMBER = {"tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bit", "bigint", "float", "double", "decimal"};

    /**
     * BO对象 不需要添加字段
     */
    String[] COLUMNNAME_NOT_ADD = {"create_by", "create_time", "del_flag", "update_by",
            "update_time", "version"};

    /**
     * BO对象 不需要编辑字段
     */
    String[] COLUMNNAME_NOT_EDIT = {"create_by", "create_time", "del_flag", "update_by",
            "update_time", "version"};

    /**
     * VO对象 不需要返回字段
     */
     String[] COLUMNNAME_NOT_LIST = {"create_by", "create_time", "del_flag", "update_by",
            "update_time", "version"};

    /**
     * BO对象 不需要查询字段
     */
     String[] COLUMNNAME_NOT_QUERY = {"id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark", "version"};

    /**
     * Entity基类字段
     */
    String[] BASE_ENTITY = {"createBy", "createTime", "updateBy", "updateTime"};

    /**
     * Tree基类字段
     */
    String[] TREE_ENTITY = {"parentName", "parentId", "children"};

    /**
     * 文本框
     */
    String HTML_INPUT = "input";

    /**
     * 文本域
     */
    String HTML_TEXTAREA = "textarea";

    /**
     * 下拉框
     */
    String HTML_SELECT = "select";

    /**
     * 单选框
     */
    String HTML_RADIO = "radio";

    /**
     * 复选框
     */
    String HTML_CHECKBOX = "checkbox";

    /**
     * 日期控件
     */
    String HTML_DATETIME = "datetime";

    /**
     * 图片上传控件
     */
    String HTML_IMAGE_UPLOAD = "imageUpload";

    /**
     * 文件上传控件
     */
    String HTML_FILE_UPLOAD = "fileUpload";

    /**
     * 富文本控件
     */
    String HTML_EDITOR = "editor";

    /**
     * 字符串类型
     */
    String TYPE_STRING = "String";

    /**
     * 整型
     */
    String TYPE_INTEGER = "Integer";

    /**
     * 长整型
     */
    String TYPE_LONG = "Long";

    /**
     * 浮点型
     */
    String TYPE_DOUBLE = "Double";

    /**
     * 高精度计算类型
     */
    String TYPE_BIGDECIMAL = "BigDecimal";

    /**
     * 时间类型
     */
    String TYPE_DATE = "Date";

    /**
     * 时间类型
     */
    String TYPE_JAVA8_DATE = "LocalDateTime";


    /**
     * 模糊查询
     */
    String QUERY_LIKE = "LIKE";

    /**
     * 相等查询
     */
    String QUERY_EQ = "EQ";

    /**
     * 需要
     */
    String REQUIRE = "1";
}
