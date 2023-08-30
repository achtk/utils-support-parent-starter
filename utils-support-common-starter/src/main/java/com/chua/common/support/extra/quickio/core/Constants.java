package com.chua.common.support.extra.quickio.core;
/**
 * 常量
 * @author CH
 */
final class Constants {
    final static String DB_PATH = "data/db/";
    final static String KV_PATH = "data/kv/";
    final static String TIN_PATH = "data/tin/";
    final static String INDEX = "index";

    final static String ILLEGAL_NAME = "The name cannot be null or empty";
    final static String SPECIAL_CHARACTER_NAME = "The name cannot contain \"/\"";
    final static String INDEX_ALREADY_EXISTS = " index already exists";
    final static String NON_INDEXED_FIELD = "Non indexed field";
    final static String FIELD_DOES_NOT_EXIST = "This field does not exist";
    final static String FIELD_DOES_NOT_SUPPORT_SORTING = "This field does not support sorting";
    final static String SORTING_FIELD_NAME_ILLEGAL = "The sort method field name cannot be null or empty";
    final static String SORTING_PARAMETER_VALUE_ILLEGAL = "The sorting parameter value can only be 1 or -1";
    final static String KEY_ALREADY_EXISTS_AND_NOT_AVAILABLE = "The new key already exists and is not available";
    final static String FIELD_NOT_NUMERICAL_TYPE = "This field is not of numerical type";
}