/*
 * Copyright (c) 2008 Nathan Sweet, Copyright (c) 2006 Ola Bini
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.chua.common.support.file.yaml.emitter;

import com.chua.common.support.file.yaml.constants.Unicode;

import java.util.regex.Pattern;

import static com.chua.common.support.file.yaml.constants.Unicode.SPACE;

/** @author Nathan Sweet
 * @author Ola Bini */
class ScalarAnalysis {
	static private final Pattern DOCUMENT_INDICATOR = Pattern.compile("^(---|\\.\\.\\.)");
	static private final String NULL_BL_T_LINEBR = "\0 \t\r\n\u0085";
	static private final String SPECIAL_INDICATOR = "#,[]{}#&*!|>'\"%@`";
	static private final String FLOW_INDICATOR = ",?[]{}";

	public final String scalar;
	public final boolean empty;
	public final boolean multiline;
	public final boolean allowFlowPlain;
	public final boolean allowBlockPlain;
	public final boolean allowSingleQuoted;
	public final boolean allowDoubleQuoted;
	public final boolean allowBlock;

	private ScalarAnalysis (String scalar, boolean empty, boolean multiline, boolean allowFlowPlain, boolean allowBlockPlain,
		boolean allowSingleQuoted, boolean allowDoubleQuoted, boolean allowBlock) {
		this.scalar = scalar;
		this.empty = empty;
		this.multiline = multiline;
		this.allowFlowPlain = allowFlowPlain;
		this.allowBlockPlain = allowBlockPlain;
		this.allowSingleQuoted = allowSingleQuoted;
		this.allowDoubleQuoted = allowDoubleQuoted;
		this.allowBlock = allowBlock;
	}

	static public ScalarAnalysis analyze (String scalar, boolean escapeUnicode) {
		if (scalar == null) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, true, false);
        }
		if ("".equals(scalar)) {
            return new ScalarAnalysis(scalar, false, false, false, false, false, true, false);
        }
		boolean blockIndicators = false, flowIndicators = false, lineBreaks = false, specialCharacters = false, inlineBreaks = false, leadingSpaces = false, leadingBreaks = false, trailingSpaces = false, trailingBreaks = false, inlineBreaksSpaces = false, mixedBreaksSpaces = false;

		if (DOCUMENT_INDICATOR.matcher(scalar).matches()) {
			blockIndicators = true;
			flowIndicators = true;
		}

		boolean preceededBySpace = true;
		boolean followedBySpace = scalar.length() == 1 || NULL_BL_T_LINEBR.indexOf(scalar.charAt(1)) != -1;
		boolean spaces = false, breaks = false, mixed = false, leading = false;

		int index = 0;

		while (index < scalar.length()) {
			char ceh = scalar.charAt(index);
			if (index == 0) {
				if (SPECIAL_INDICATOR.indexOf(ceh) != -1) {
					flowIndicators = true;
					blockIndicators = true;
				}
				if (ceh == '?' || ceh == ':') {
					flowIndicators = true;
					if (followedBySpace) {
                        blockIndicators = true;
                    }
				}
				if (ceh == '-' && followedBySpace) {
					flowIndicators = true;
					blockIndicators = true;
				}
			} else {
				if (FLOW_INDICATOR.indexOf(ceh) != -1) {
                    flowIndicators = true;
                }
				if (ceh == ':') {
					flowIndicators = true;
					if (followedBySpace) {
                        blockIndicators = true;
                    }
				}
				if (ceh == '#' && preceededBySpace) {
					flowIndicators = true;
					blockIndicators = true;
				}
			}
			if (ceh == '\n' || Unicode.NEXT_LINE == ceh) {
                lineBreaks = true;
            }
			if (escapeUnicode) {
				boolean v = ceh != '\n' && ceh != '\t' && !(SPACE <= ceh && ceh <= Unicode.TILDE);
				if (v) {
                    specialCharacters = true;
                }
			}
			if (' ' == ceh || '\n' == ceh || Unicode.NEXT_LINE == ceh) {
				if (spaces && breaks) {
					if (ceh != ' ') {
                        mixed = true;
                    }
				} else if (spaces) {
					if (ceh != ' ') {
						breaks = true;
						mixed = true;
					}
				} else if (breaks) {
					if (ceh == ' ') {
                        spaces = true;
                    }
				} else {
					leading = index == 0;
					if (ceh == ' ') {
                        spaces = true;
                    } else {
                        breaks = true;
                    }
				}
			} else if (spaces || breaks) {
				if (leading) {
					if (spaces && breaks) {
                        mixedBreaksSpaces = true;
                    } else if (spaces) {
                        leadingSpaces = true;
                    } else if (breaks) {
						leadingBreaks = true;
					}
				} else if (mixed) {
                    mixedBreaksSpaces = true;
                } else if (spaces && breaks) {
                    inlineBreaksSpaces = true;
                } else if (spaces) {
					// inlineSpaces = true;
				} else if (breaks) {
					inlineBreaks = true;
				}
				spaces = breaks = mixed = leading = false;
			}

			boolean b = (spaces || breaks) && index == scalar.length() - 1;
			if (b) {
				if (spaces && breaks) {
                    mixedBreaksSpaces = true;
                } else if (spaces) {
					trailingSpaces = true;
					if (leading) {
                        leadingSpaces = true;
                    }
				} else if (breaks) {
					trailingBreaks = true;
					if (leading) {
                        leadingBreaks = true;
                    }
				}
				spaces = breaks = mixed = leading = false;
			}
			index++;
			preceededBySpace = NULL_BL_T_LINEBR.indexOf(ceh) != -1;
			followedBySpace = index + 1 >= scalar.length() || NULL_BL_T_LINEBR.indexOf(scalar.charAt(index + 1)) != -1;
		}
		boolean allowFlowPlain = true, allowBlockPlain = true, allowSingleQuoted = true, allowDoubleQuoted = true, allowBlock = true;

		if (leadingSpaces || leadingBreaks || trailingSpaces) {
            allowFlowPlain = allowBlockPlain = allowBlock = false;
        }

		if (trailingBreaks) {
            allowFlowPlain = allowBlockPlain = false;
        }

		if (inlineBreaksSpaces) {
            allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
        }

		if (mixedBreaksSpaces || specialCharacters) {
            allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false;
        }

		if (inlineBreaks) {
            allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
        }

		if (trailingBreaks) {
            allowSingleQuoted = false;
        }

		if (lineBreaks) {
            allowFlowPlain = allowBlockPlain = false;
        }

		if (flowIndicators) {
            allowFlowPlain = false;
        }

		if (blockIndicators) {
            allowBlockPlain = false;
        }

		return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowDoubleQuoted,
			allowBlock);
	}
}
