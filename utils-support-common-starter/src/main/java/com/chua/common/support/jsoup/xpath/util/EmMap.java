package com.chua.common.support.jsoup.xpath.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author 汪浩淼 [ et.tw@163.com ]
 * @author CH
 * @since 14-3-11
 */
public class EmMap {
    public final Map<String,ScopeEm> scopeEmMap = new HashMap<String, ScopeEm>();
    public final Map<String,OpEm> opEmMap = new HashMap<String, OpEm>();
    public final Set<Character> commOpChar = new HashSet<Character>();
    private static final EmMap OUR_INSTANCE = new EmMap();

    public static EmMap getInstance() {
        return OUR_INSTANCE;
    }

    private EmMap() {
        scopeEmMap.put("/",ScopeEm.INCHILREN);
        scopeEmMap.put("//",ScopeEm.RECURSIVE);
        scopeEmMap.put("./",ScopeEm.CUR);
        scopeEmMap.put(".//",ScopeEm.CURREC);

        opEmMap.put("+",OpEm.PLUS);
        opEmMap.put("-",OpEm.MINUS);
        opEmMap.put("=",OpEm.EQ);
        opEmMap.put("!=",OpEm.NE);
        opEmMap.put(">",OpEm.GT);
        opEmMap.put("<",OpEm.LT);
        opEmMap.put(">=",OpEm.GE);
        opEmMap.put("<=",OpEm.LE);
        opEmMap.put("^=",OpEm.START_WITH);
        opEmMap.put("$=",OpEm.END_WITH);
        opEmMap.put("*=",OpEm.CONTAIN);
        opEmMap.put("~=",OpEm.REGEX);

        commOpChar.add('+');
        commOpChar.add('-');
        commOpChar.add('=');
        commOpChar.add('*');
        commOpChar.add('^');
        commOpChar.add('$');
        commOpChar.add('~');
        commOpChar.add('>');
        commOpChar.add('<');
        commOpChar.add('!');
    }
}
