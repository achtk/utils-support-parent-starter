package com.alibaba.json.parser.deserializer;

import com.alibaba.json.parser.DefaultJSONParser;
import com.alibaba.json.parser.JSONLexerBase;
import com.alibaba.json.parser.JSONToken;
import com.alibaba.json.parser.SymbolTable;
import com.alibaba.json.parser.deserializer.ObjectDeserializer;
import com.alibaba.json.JSONException;
import com.alibaba.json.JSONPObject;

import java.lang.reflect.Type;

/**
 * Created by wenshao on 21/02/2017.
 */
public class JSONPDeserializer implements ObjectDeserializer {
    public static final JSONPDeserializer instance = new JSONPDeserializer();

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexerBase lexer = (JSONLexerBase) parser.getLexer();

        SymbolTable symbolTable = parser.getSymbolTable();

        String funcName = lexer.scanSymbolUnQuoted(symbolTable);
        lexer.nextToken();

        int tok = lexer.token();

        if (tok == JSONToken.DOT) {
            String name = lexer.scanSymbolUnQuoted(parser.getSymbolTable());
            funcName += ".";
            funcName += name;
            lexer.nextToken();
            tok = lexer.token();
        }

        com.alibaba.json.JSONPObject jsonp = new JSONPObject(funcName);

        if (tok != JSONToken.LPAREN) {
            throw new com.alibaba.json.JSONException("illegal jsonp : " + lexer.info());
        }
        lexer.nextToken();
        for (;;) {
            Object arg = parser.parse();
            jsonp.addParameter(arg);

            tok = lexer.token();
            if (tok == JSONToken.COMMA) {
                lexer.nextToken();
            } else if (tok == JSONToken.RPAREN) {
                lexer.nextToken();
                break;
            } else {
                throw new JSONException("illegal jsonp : " + lexer.info());
            }
         }
        tok = lexer.token();
        if (tok == JSONToken.SEMI) {
            lexer.nextToken();
        }

        return (T) jsonp;
    }

    public int getFastMatchToken() {
        return 0;
    }
}
