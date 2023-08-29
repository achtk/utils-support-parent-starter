package com.chua.common.support.jsoup.xpath.core;

import com.chua.common.support.jsoup.xpath.model.Node;
import com.chua.common.support.jsoup.xpath.model.Predicate;
import com.chua.common.support.jsoup.xpath.util.EmMap;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET_CHAR;

/**
 * 用于生成xpath语法树的有限状态机
 *
 * @author 汪浩淼 [et.tw@163.com]@au
 * @since 13-12-26
 */
public class NodeTreeBuilderStateMachine {
    BuilderState state = BuilderState.SCOPE;
    Context context = new Context();
    int cur = 0;
    StringBuilder accum = new StringBuilder();

    enum BuilderState {
        /**
         * scope
         */
        SCOPE {
            @Override
            public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
                if (stateMachine.cur == xpath.length) {
                    stateMachine.state = END;
                }
                while (stateMachine.cur < xpath.length) {
                    if (!(xpath[stateMachine.cur] == '/' || xpath[stateMachine.cur] == '.')) {
                        stateMachine.state = AXIS;
                        Node xn = new Node();
                        stateMachine.context.xpathTr.add(xn);
                        xn.setScopeEm(EmMap.getInstance().scopeEmMap.get(stateMachine.accum.toString()));
                        stateMachine.accum = new StringBuilder();
                        break;
                    }
                    stateMachine.accum.append(xpath[stateMachine.cur]);
                    stateMachine.cur += 1;
                }
            }
        },
        /**
         * axis
         */
        AXIS {
            @Override
            public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
                int curtmp = stateMachine.cur;
                StringBuilder accumTmp = new StringBuilder();
                while (curtmp < xpath.length && xpath[curtmp] != SYMBOL_LEFT_SQUARE_BRACKET_CHAR && xpath[curtmp] != '/') {
                    if (xpath[curtmp] == ':') {
                        stateMachine.context.xpathTr.getLast().setAxis(accumTmp.toString());
                        stateMachine.cur = curtmp + 2;
                        stateMachine.state = TAG;
                        break;
                    }
                    accumTmp.append(xpath[curtmp]);
                    curtmp += 1;
                }
                stateMachine.state = TAG;
            }
        },
        /**
         * tag
         */
        TAG {
            @Override
            public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
                while (stateMachine.cur < xpath.length && xpath[stateMachine.cur] != SYMBOL_LEFT_SQUARE_BRACKET_CHAR && xpath[stateMachine.cur] != '/') {
                    stateMachine.accum.append(xpath[stateMachine.cur]);
                    stateMachine.cur += 1;
                }
                stateMachine.context.xpathTr.getLast().setTagName(stateMachine.accum.toString());
                stateMachine.accum = new StringBuilder();
                if (stateMachine.cur == xpath.length) {
                    stateMachine.state = END;
                } else if (xpath[stateMachine.cur] == '/') {
                    stateMachine.state = SCOPE;
                } else if (xpath[stateMachine.cur] == SYMBOL_LEFT_SQUARE_BRACKET_CHAR) {
                    stateMachine.state = PREDICATE;
                }
            }
        },
        /**
         * predicate
         */
        PREDICATE {
            @Override
            public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
                int deep = 0;
                stateMachine.cur += 1;
                while (!(xpath[stateMachine.cur] == ']' && deep == 0)) {
                    if (xpath[stateMachine.cur] == '[') {
                        deep += 1;
                    }
                    if (xpath[stateMachine.cur] == ']') {
                        deep -= 1;
                    }
                    stateMachine.accum.append(xpath[stateMachine.cur]);
                    stateMachine.cur += 1;
                }
                Predicate predicate = stateMachine.genPredicate(stateMachine.accum.toString());
                stateMachine.context.xpathTr.getLast().setPredicate(predicate);
                stateMachine.accum = new StringBuilder();
                if (stateMachine.cur < xpath.length - 1) {
                    stateMachine.cur += 1;
                    stateMachine.state = SCOPE;
                } else {
                    stateMachine.state = END;
                }
            }
        },
        /**
         * end
         */
        END {
            @Override
            public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
            }
        };

        public void parser(NodeTreeBuilderStateMachine stateMachine, char[] xpath) {
        }
    }

    private static final String REG = ".+(\\+|=|-|>|<|>=|<=|^=|\\*=|$=|~=|!=)[^']+";
    private static final String REG1 = ".+(\\+|=|-|>|<|>=|<=|^=|\\*=|$=|~=|!=)'.+'";

    /**
     * 根据谓语字符串初步生成谓语体
     *
     * @param pre pre
     * @return Predicate
     */
    public Predicate genPredicate(String pre) {
        StringBuilder op = new StringBuilder();
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();
        Predicate predicate = new Predicate();
        char[] preArray = pre.toCharArray();
        int index = preArray.length - 1;
        int argDeep = 0;
        int opFlag = 0;
        if (pre.matches(REG1)) {
            while (index >= 0) {
                char tmp = preArray[index];
                if (tmp == '\'') {
                    argDeep += 1;
                }
                if (argDeep == 1 && tmp != '\'') {
                    right.insert(0, tmp);
                } else if (argDeep == 2 && EmMap.getInstance().commOpChar.contains(tmp)) {
                    op.insert(0, tmp);
                    opFlag = 1;
                } else if (argDeep >= 2 && opFlag > 0) {
                    argDeep++;//取完操作符后剩下的都属于left
                    left.insert(0, tmp);
                }
                index -= 1;
            }
        } else if (pre.matches(REG)) {
            while (index >= 0) {
                char tmp = preArray[index];
                if (opFlag == 0 && EmMap.getInstance().commOpChar.contains(tmp)) {
                    op.insert(0, tmp);
                } else {
                    if (op.length() > 0) {
                        left.insert(0, tmp);
                        opFlag = 1;
                    } else {
                        right.insert(0, tmp);
                    }
                }
                index -= 1;
            }
        }

        predicate.setOpEm(EmMap.getInstance().opEmMap.get(op.toString()));
        predicate.setLeft(left.toString());
        predicate.setRight(right.toString());
        predicate.setValue(pre);
        return predicate;
    }
}
