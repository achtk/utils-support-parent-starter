package com.chua.common.support.lang.spider.xsoup.w3c;

import com.chua.common.support.jsoup.nodes.Attribute;
import org.w3c.dom.*;

/**
 * @author code4crafer@gmail.com
 */
public class ElementAdaptor extends BaseNodeAdaptor implements Element {

    private final com.chua.common.support.jsoup.nodes.Element element;

    public ElementAdaptor(com.chua.common.support.jsoup.nodes.Element element) {
        this.element = element;
    }

    @Override
    public String getTagName() {
        return element.tagName();
    }

    @Override
    public String getAttribute(String name) {
        return element.attr(name);
    }

    @Override
    public Attr getAttributeNode(String name) {
        if (element.attr(name) == null) {
            return null;
        }
        return NodeAdaptors.getAttr(new Attribute(name, element.attr(name)), element);
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        return NodeAdaptors.getNodeList(element.getElementsByTag(name));
    }

    @Override
    public boolean hasAttribute(String name) {
        return element.hasAttr(name);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return DummyTypeInfo.getInstance();
    }

    @Override
    public String getNodeName() {
        return element.nodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return element.outerHtml();
    }

    @Override
    public short getNodeType() {
        return ELEMENT_NODE;
    }

    @Override
    public Node getParentNode() {
        return NodeAdaptors.getElement(element.parent());
    }

    @Override
    public NodeList getChildNodes() {
        return NodeAdaptors.getNodeList(element.childNodes());
    }

    @Override
    public Node getFirstChild() {
        if (element.children().isEmpty()) {
            return null;
        }
        return NodeAdaptors.getNode(element.child(0));
    }

    @Override
    public Node getLastChild() {
        if (element.children().isEmpty()) {
            return null;
        }
        return NodeAdaptors.getNode(element.child(element.childNodeSize()));
    }

    @Override
    public Node getPreviousSibling() {
        return NodeAdaptors.getNode(element.previousSibling());
    }

    @Override
    public Node getNextSibling() {
        return NodeAdaptors.getNode(element.nextSibling());
    }

    @Override
    public NamedNodeMap getAttributes() {
        return NodeAdaptors.getNamedNodeMap(NodeAdaptors.getAttributes(element.attributes(), element));
    }

    @Override
    public String getTextContent() throws DOMException {
        return element.text();
    }

    @Override
    public Document getOwnerDocument() {
        return NodeAdaptors.getDocument(element.ownerDocument());
    }

    @Override
    public boolean hasChildNodes() {
        return !element.children().isEmpty();
    }

    @Override
    public Node cloneNode(boolean deep) {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return true;
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    @Override
    public boolean isSameNode(Node other) {
        return false;
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return false;
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttributeNS(String s, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttributeNS(String s, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNS(String namespace, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    /*--------------------- NS not supported ----------------*/

    @Override
    public String getAttributeNS(String namespace, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr getAttributeNodeNS(String namespace, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
