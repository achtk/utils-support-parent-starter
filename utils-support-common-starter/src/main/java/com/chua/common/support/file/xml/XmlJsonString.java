package com.chua.common.support.file.xml;

/*
Public Domain.
 */

/**
 * json str
 * @author Administrator
 */
public interface XmlJsonString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    String toJsonString();
}
