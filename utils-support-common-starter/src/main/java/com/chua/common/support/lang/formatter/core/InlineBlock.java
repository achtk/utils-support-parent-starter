package com.chua.common.support.lang.formatter.core;

import com.chua.common.support.lang.formatter.core.util.JSLikeList;

/**
 * Bookkeeper for inline blocks.
 *
 * <p>Inline blocks are parenthized expressions that are shorter than maxColumnLength. These blocks
 * are formatted on a single line, unlike longer parenthized expressions where open-parenthesis
 * causes newline and increase of indentation.
 */
class InlineBlock {

  private int level;
  private final int maxColumnLength;

  InlineBlock(int maxColumnLength) {
    this.maxColumnLength = maxColumnLength;
    this.level = 0;
  }

  /**
   * Begins inline block when lookahead through upcoming tokens determines that the block would be
   * smaller than INLINE_MAX_LENGTH.
   *
   * @param tokens Array of all tokens
   * @param index Current token position
   */
  void beginIfPossible(JSLikeList<Token> tokens, int index) {
    if (this.level == 0 && this.isInlineBlock(tokens, index)) {
      this.level = 1;
    } else if (this.level > 0) {
      this.level++;
    } else {
      this.level = 0;
    }
  }

  /** Finishes current inline block. There might be several nested ones. */
  public void end() {
    this.level--;
  }

  /**
   * True when inside an inline block
   *
   * @return {Boolean}
   */
  boolean isActive() {
    return this.level > 0;
  }

  private boolean isInlineBlock(JSLikeList<Token> tokens, int index) {
    int length = 0;
    int level = 0;

    for (int i = index; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      length += token.value.length();

      // Overran max length
      if (length > maxColumnLength) {
        return false;
      }

      if (token.type == TokenTypes.OPEN_PAREN) {
        level++;
      } else if (token.type == TokenTypes.CLOSE_PAREN) {
        level--;
        if (level == 0) {
          return true;
        }
      }

      if (this.isForbiddenToken(token)) {
        return false;
      }
    }
    return false;
  }

  private boolean isForbiddenToken(Token token) {
    return token.type == TokenTypes.RESERVED_TOP_LEVEL
        || token.type == TokenTypes.RESERVED_NEWLINE
        ||
        //                originally `TokenTypes.LINE_COMMENT` but this symbol is not defined
        //                token.type == TokenTypes.LINE_COMMENT ||
        token.type == TokenTypes.BLOCK_COMMENT
        || ";".equals(token.value);
  }
}
