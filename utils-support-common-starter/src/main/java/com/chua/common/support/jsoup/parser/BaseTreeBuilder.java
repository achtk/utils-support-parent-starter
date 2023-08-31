package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 树构建器
 *
 * @author Jonathan Hedley
 * @date 2023/08/31
 */
abstract class BaseTreeBuilder {
    protected Parser parser;
    CharacterReader reader;
    Tokeniser tokeniser;
    protected Document doc;
    protected ArrayList<Element> stack;
    protected String baseUri;
    protected BaseToken currentToken;
    protected ParseSettings settings;
    protected Map<String, Tag> seenTags;
    private BaseToken.StartTag start = new BaseToken.StartTag();
    private BaseToken.EndTag end = new BaseToken.EndTag();

    /**
     * 默认设置
     *
     * @return 默认设置
     */
    abstract ParseSettings defaultSettings();

    private boolean trackSourceRange;

    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        Validate.notNullParam(input, "input");
        Validate.notNullParam(baseUri, "baseUri");
        Validate.notNull(parser);

        doc = new Document(baseUri);
        doc.parser(parser);
        this.parser = parser;
        settings = parser.settings();
        reader = new CharacterReader(input);
        trackSourceRange = parser.isTrackPosition();
        reader.trackNewlines(parser.isTrackErrors() || trackSourceRange);
        currentToken = null;
        tokeniser = new Tokeniser(reader, parser.getErrors());
        stack = new ArrayList<>(32);
        seenTags = new LinkedHashMap<>();
        this.baseUri = baseUri;
    }

    Document parse(Reader input, String baseUri, Parser parser) {
        initialiseParse(input, baseUri, parser);
        runParser();

        reader.close();
        reader = null;
        tokeniser = null;
        stack = null;
        seenTags = null;

        return doc;
    }

    /**
     * Create a new copy of this TreeBuilder
     *
     * @return copy, ready for a new parse
     */
    abstract BaseTreeBuilder newInstance();

    /**
     * 解析一个HTML片段并返回一个DOM元素。
     *
     * @param inputFragment 片段
     * @param context       内容
     * @param baseUri       地址
     * @param parser        解析器
     * @return 结果
     */
    abstract List<Node> parseFragment(String inputFragment, Element context, String baseUri, Parser parser);

    protected void runParser() {
        final Tokeniser tokeniser = this.tokeniser;
        final BaseToken.TokenType eof = BaseToken.TokenType.EOF;

        while (true) {
            BaseToken token = tokeniser.read();
            process(token);
            token.reset();

            if (token.type == eof) {
                break;
            }
        }
    }

    /**
     * 处理进度
     *
     * @param token token
     * @return 进度
     */
    protected abstract boolean process(BaseToken token);

    protected boolean processStartTag(String name) {
        final BaseToken.StartTag start = this.start;
        if (currentToken == start) {
            return process(new BaseToken.StartTag().name(name));
        }
        return process(start.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        final BaseToken.StartTag start = this.start;
        if (currentToken == start) {
            return process(new BaseToken.StartTag().nameAttr(name, attrs));
        }
        start.reset();
        start.nameAttr(name, attrs);
        return process(start);
    }

    protected boolean processEndTag(String name) {
        if (currentToken == end) {
            return process(new BaseToken.EndTag().name(name));
        }
        return process(end.reset().name(name));
    }


    /**
     * Get the current element (last on the stack). If all items have been removed, returns the document instead
     * (which might not actually be on the stack; use stack.size() == 0 to test if required.
     *
     * @return the last element on the stack, if any; or the root document
     */
    protected Element currentElement() {
        int size = stack.size();
        return size > 0 ? stack.get(size - 1) : doc;
    }

    /**
     * Checks if the Current Element's normal name equals the supplied name.
     *
     * @param normalName name to check
     * @return true if there is a current element on the stack, and its name equals the supplied
     */
    protected boolean currentElementIs(String normalName) {
        if (stack.size() == 0) {
            return false;
        }
        Element current = currentElement();
        return current != null && current.normalName().equals(normalName);
    }

    /**
     * If the parser is tracking errors, add an error at the current position.
     *
     * @param msg error message
     */
    protected void error(String msg) {
        error(msg, (Object[]) null);
    }

    /**
     * If the parser is tracking errors, add an error at the current position.
     *
     * @param msg  error message template
     * @param args template arguments
     */
    protected void error(String msg, Object... args) {
        ParseErrorList errors = parser.getErrors();
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, msg, args));
        }
    }

    /**
     * (An internal method, visible for Element. For HTML parse, signals that script and style text should be treated as
     * Data Nodes).
     */
    protected boolean isContentForTagData(String normalName) {
        return false;
    }

    protected Tag tagFor(String tagName, ParseSettings settings) {
        Tag tag = seenTags.get(tagName);
        if (tag == null) {
            tag = Tag.valueOf(tagName, settings);
            seenTags.put(tagName, tag);
        }
        return tag;
    }

    /**
     * Called by implementing TreeBuilders when a node has been inserted. This implementation includes optionally tracking
     * the source range of the node.
     *
     * @param node  the node that was just inserted
     * @param token the (optional) token that created this node
     */
    protected void onNodeInserted(Node node, BaseToken token) {
        trackNodePosition(node, token, true);
    }

    /**
     * Called by implementing TreeBuilders when a node is explicitly closed. This implementation includes optionally
     * tracking the closing source range of the node.
     *
     * @param node  the node being closed
     * @param token the end-tag token that closed this node
     */
    protected void onNodeClosed(Node node, BaseToken token) {
        trackNodePosition(node, token, false);
    }

    private void trackNodePosition(Node node, BaseToken token, boolean start) {
        if (trackSourceRange && token != null) {
            int startPos = token.startPos();
            if (startPos == BaseToken.UNSET) {
                return;
            }

            Range.Position startRange = new Range.Position(startPos, reader.lineNumber(startPos), reader.columnNumber(startPos));
            int endPos = token.endPos();
            Range.Position endRange = new Range.Position(endPos, reader.lineNumber(endPos), reader.columnNumber(endPos));
            Range range = new Range(startRange, endRange);
            range.track(node, start);
        }
    }
}
