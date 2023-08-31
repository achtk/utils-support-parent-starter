
package com.chua.common.support.lang.template.basis.parsing;

import com.chua.common.support.lang.template.basis.BasisTemplate;
import com.chua.common.support.lang.template.basis.Error;
import com.chua.common.support.lang.template.basis.TemplateLoader.Source;
import com.chua.common.support.lang.template.basis.parsing.Ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SEMICOLON;
import static com.chua.common.support.constant.NameConstant.*;

/**
 * Parses a {@link Source} into a {@link BasisTemplate}. The implementation is a simple recursive descent parser with a lookahead of
 *
 * @author Administrator
 **/
public class Parser {

	private static final String IFS = "if";
	private static final String FOR = "for";
	private static final String WHILE = "while";
	private static final String CONTINUE = "continue";
	private static final String BREAK = "break";
	private static final String MACRO = "macro";
	private static final String INCLUDE = "include";
	private static final String RETURN = "return";

	/** Parses a {@link Source} into a {@link BasisTemplate}. **/
	public ParserResult parse(Source source) {
		List<AbstractNode> nodes = new ArrayList<>();
		Macros macros = new Macros();
		List<Include> includes = new ArrayList<Include>();
		List<IncludeRaw> rawIncludes = new ArrayList<IncludeRaw>();
		TokenStream stream = new TokenStream(new Tokenizer().tokenize(source));

		while (stream.hasMore()) {
			nodes.add(parseStatement(stream, true, macros, includes, rawIncludes));
		}
		return new ParserResult(nodes, macros, includes, rawIncludes);
	}

	/** Parse a statement, which may either be a text block, if statement, for statement, while statement, macro definition,
	 * include statement or an expression. **/
	private AbstractNode parseStatement (TokenStream tokens, boolean allowMacros, Macros macros, List<Include> includes, List<IncludeRaw> rawIncludes) {
		AbstractNode result = null;

		if (tokens.match(TokenType.TextBlock, false)) {
			result = new Text(tokens.consume().getSpan());
		} else if (tokens.match(IFS, false)) {
			result = parseIfStatement(tokens, includes, rawIncludes);
		} else if (tokens.match(FOR, false)) {
			result = parseForStatement(tokens, includes, rawIncludes);
		} else if (tokens.match(WHILE, false)) {
			result = parseWhileStatement(tokens, includes, rawIncludes);
		} else if (tokens.match(CONTINUE, false)) {
			result = new Continue(tokens.consume().getSpan());
		} else if (tokens.match(BREAK, false)) {
			result = new Break(tokens.consume().getSpan());
		} else if (tokens.match(MACRO, false)) {
			if (!allowMacros) {
				Error.error("Macros can only be defined at the top level of a template.", tokens.consume().getSpan());
				result = null;
			} else {
				Macro macro = parseMacro(tokens, includes, rawIncludes);
				macros.put(macro.getName().getText(), macro);
				result = macro;
			}
		} else if (tokens.match(INCLUDE, false)) {
			result = parseInclude(tokens, includes, rawIncludes);
		} else if (tokens.match(RETURN, false)) {
			result = parseReturn(tokens);
		} else {
			result = parseExpression(tokens);
		}


		while (tokens.match(SYMBOL_SEMICOLON, true)) {
			;
		}

		return result;
	}

	private IfStatement parseIfStatement (TokenStream stream, List<Include> includes, List<IncludeRaw> rawIncludes) {
		Span openingIf = stream.expect("if").getSpan();

		AbstractExpression condition = parseExpression(stream);

		List<AbstractNode> trueBlock = new ArrayList<>();
		while (stream.hasMore() && !stream.match(false, ELSEIF, ELSE, END)) {
			trueBlock.add(parseStatement(stream, false, null, includes, rawIncludes));
		}

		List<IfStatement> elseIfs = new ArrayList<IfStatement>();
		while (stream.hasMore() && stream.match(false, ELSEIF)) {
			Span elseIfOpening = stream.expect(ELSEIF).getSpan();

			AbstractExpression elseIfCondition = parseExpression(stream);

			List<AbstractNode> elseIfBlock = new ArrayList<>();
			while (stream.hasMore() && !stream.match(false, ELSEIF, ELSE, END)) {
				elseIfBlock.add(parseStatement(stream, false, null, includes, rawIncludes));
			}

			Span elseIfSpan = new Span(elseIfOpening, elseIfBlock.size() > 0 ? elseIfBlock.get(elseIfBlock.size() - 1).getSpan() : elseIfOpening);
			elseIfs.add(new IfStatement(elseIfSpan, elseIfCondition, elseIfBlock, new ArrayList<IfStatement>(), new ArrayList<AbstractNode>()));
		}

		List<AbstractNode> falseBlock = new ArrayList<>();
		if (stream.match(ELSE, true)) {
			while (stream.hasMore() && !stream.match(false, END)) {
				falseBlock.add(parseStatement(stream, false, null, includes, rawIncludes));
			}
		}

		Span closingEnd = stream.expect(END).getSpan();

		return new IfStatement(new Span(openingIf, closingEnd), condition, trueBlock, elseIfs, falseBlock);
	}

	private ForStatement parseForStatement (TokenStream stream, List<Include> includes, List<IncludeRaw> rawIncludes) {
		Span openingFor = stream.expect("for").getSpan();

		Span index = null;
		Span value = stream.expect(TokenType.Identifier).getSpan();

		if (stream.match(TokenType.Comma, true)) {
			index = value;
			value = stream.expect(TokenType.Identifier).getSpan();
		}

		stream.expect("in");

		AbstractExpression mapOrArray = parseExpression(stream);

		List<AbstractNode> body = new ArrayList<>();
		while (stream.hasMore() && !stream.match(false, END)) {
			body.add(parseStatement(stream, false, null, includes, rawIncludes));
		}

		Span closingEnd = stream.expect(END).getSpan();

		return new ForStatement(new Span(openingFor, closingEnd), index != null ? index : null, value, mapOrArray, body);
	}

	private WhileStatement parseWhileStatement (TokenStream stream, List<Include> includes, List<IncludeRaw> rawIncludes) {
		Span openingWhile = stream.expect(WHILE).getSpan();

		AbstractExpression condition = parseExpression(stream);

		List<AbstractNode> body = new ArrayList<>();
		while (stream.hasMore() && !stream.match(false, END)) {
			body.add(parseStatement(stream, false, null, includes, rawIncludes));
		}

		Span closingEnd = stream.expect(END).getSpan();

		return new WhileStatement(new Span(openingWhile, closingEnd), condition, body);
	}

	private Macro parseMacro (TokenStream stream, List<Include> includes, List<IncludeRaw> rawIncludes) {
		Span openingWhile = stream.expect(MACRO).getSpan();

		Span name = stream.expect(TokenType.Identifier).getSpan();

		List<Span> argumentNames = parseArgumentNames(stream);

		stream.expect(TokenType.RightParantheses);

		List<AbstractNode> body = new ArrayList<>();
		while (stream.hasMore() && !stream.match(false, END)) {
			body.add(parseStatement(stream, false, null, includes, rawIncludes));
		}

		Span closingEnd = stream.expect(END).getSpan();

		return new Macro(new Span(openingWhile, closingEnd), name, argumentNames, body);
	}

	/** Does not consume the closing parentheses. **/
	private List<Span> parseArgumentNames (TokenStream stream) {
		stream.expect(TokenType.LeftParantheses);
		List<Span> arguments = new ArrayList<Span>();
		while (stream.hasMore() && !stream.match(TokenType.RightParantheses, false)) {
			arguments.add(stream.expect(TokenType.Identifier).getSpan());
			if (!stream.match(TokenType.RightParantheses, false)) {
				stream.expect(TokenType.Comma);
			}
		}
		return arguments;
	}

	private AbstractNode parseInclude (TokenStream stream, List<Include> includes, List<IncludeRaw> rawIncludes) {
		Span openingInclude = stream.expect(INCLUDE).getSpan();
		if (stream.match(RAW, true)) {
			Span path = stream.expect(TokenType.StringLiteral).getSpan();
			IncludeRaw rawInclude = new IncludeRaw(new Span(openingInclude, path), path);
			rawIncludes.add(rawInclude);
			return rawInclude;
		}

		Span path = stream.expect(TokenType.StringLiteral).getSpan();
		Span closing = path;

		Include include = null;
		if (stream.match(WITH, true)) {
			Map<Span, AbstractExpression> context = parseMap(stream);
			closing = stream.expect(TokenType.RightParantheses).getSpan();
			include = new Include(new Span(openingInclude, closing), path, context, false, null);
		} else if (stream.match(AS, true)) {
			Span alias = stream.expect(TokenType.Identifier).getSpan();
			closing = alias;
			include = new Include(new Span(openingInclude, closing), path, null, true, alias);
		} else {
			include = new Include(new Span(openingInclude, closing), path, new HashMap<>(1 << 4), false, null);
		}
		includes.add(include);
		return include;
	}

	/** Does not consume the closing parentheses. **/
	private Map<Span, AbstractExpression> parseMap (TokenStream stream) {
		stream.expect(TokenType.LeftParantheses);
		Map<Span, AbstractExpression> map = new HashMap<>(1 << 4);
		while (stream.hasMore() && !stream.match(TokenType.RightParantheses, false)) {
			Span key = stream.expect(TokenType.Identifier).getSpan();
			stream.expect(TokenType.Colon);
			map.put(key, parseExpression(stream));
			if (!stream.match(TokenType.RightParantheses, false)) {
				stream.expect(TokenType.Comma);
			}
		}
		return map;
	}

	private AbstractExpression parseExpression (TokenStream stream) {
		return parseTernaryOperator(stream);
	}

	private AbstractExpression parseTernaryOperator (TokenStream stream) {
		AbstractExpression condition = parseBinaryOperator(stream, 0);
		if (stream.match(TokenType.Questionmark, true)) {
			AbstractExpression trueExpression = parseTernaryOperator(stream);
			stream.expect(TokenType.Colon);
			AbstractExpression falseExpression = parseTernaryOperator(stream);
			return new TernaryOperation(condition, trueExpression, falseExpression);
		} else {
			return condition;
		}
	}

	TokenType[][] binaryOperatorPrecedence = new TokenType[][] {new TokenType[] {TokenType.Assignment},
		new TokenType[] {TokenType.Or, TokenType.And, TokenType.Xor}, new TokenType[] {TokenType.Equal, TokenType.NotEqual},
		new TokenType[] {TokenType.Less, TokenType.LessEqual, TokenType.Greater, TokenType.GreaterEqual}, new TokenType[] {TokenType.Plus, TokenType.Minus},
		new TokenType[] {TokenType.ForwardSlash, TokenType.Asterisk, TokenType.Percentage}};

	private AbstractExpression parseBinaryOperator (TokenStream stream, int level) {
		int nextLevel = level + 1;
		AbstractExpression left = nextLevel == binaryOperatorPrecedence.length ? parseUnaryOperator(stream) : parseBinaryOperator(stream, nextLevel);

		TokenType[] operators = binaryOperatorPrecedence[level];
		while (stream.hasMore() && stream.match(false, operators)) {
			Token operator = stream.consume();
			AbstractExpression right = nextLevel == binaryOperatorPrecedence.length ? parseUnaryOperator(stream) : parseBinaryOperator(stream, nextLevel);
			left = new BinaryOperation(left, operator, right);
		}

		return left;
	}

	TokenType[] unaryOperators = new TokenType[] {TokenType.Not, TokenType.Plus, TokenType.Minus};

	private AbstractExpression parseUnaryOperator (TokenStream stream) {
		if (stream.match(false, unaryOperators)) {
			return new UnaryOperation(stream.consume(), parseUnaryOperator(stream));
		} else {
			if (stream.match(TokenType.LeftParantheses, true)) {
				AbstractExpression expression = parseExpression(stream);
				stream.expect(TokenType.RightParantheses);
				return expression;
			} else {
				return parseAccessOrCallOrLiteral(stream);
			}
		}
	}

	private AbstractExpression parseAccessOrCallOrLiteral (TokenStream stream) {
		if (stream.match(TokenType.Identifier, false)) {
			return parseAccessOrCall(stream);
		} else if (stream.match(TokenType.LeftCurly, false)) {
			return parseMapLiteral(stream);
		} else if (stream.match(TokenType.LeftBracket, false)) {
			return parseListLiteral(stream);
		} else if (stream.match(TokenType.StringLiteral, false)) {
			return new StringLiteral(stream.expect(TokenType.StringLiteral).getSpan(), false);
		} else if (stream.match(TokenType.RawStringLiteral, false)) {
			return new StringLiteral(stream.expect(TokenType.RawStringLiteral).getSpan(), true);
		}else if (stream.match(TokenType.BooleanLiteral, false)) {
			return new BooleanLiteral(stream.expect(TokenType.BooleanLiteral).getSpan());
		} else if (stream.match(TokenType.DoubleLiteral, false)) {
			return new DoubleLiteral(stream.expect(TokenType.DoubleLiteral).getSpan());
		} else if (stream.match(TokenType.FloatLiteral, false)) {
			return new FloatLiteral(stream.expect(TokenType.FloatLiteral).getSpan());
		} else if (stream.match(TokenType.ByteLiteral, false)) {
			return new ByteLiteral(stream.expect(TokenType.ByteLiteral).getSpan());
		} else if (stream.match(TokenType.ShortLiteral, false)) {
			return new ShortLiteral(stream.expect(TokenType.ShortLiteral).getSpan());
		} else if (stream.match(TokenType.IntegerLiteral, false)) {
			return new IntegerLiteral(stream.expect(TokenType.IntegerLiteral).getSpan());
		} else if (stream.match(TokenType.LongLiteral, false)) {
			return new LongLiteral(stream.expect(TokenType.LongLiteral).getSpan());
		} else if (stream.match(TokenType.CharacterLiteral, false)) {
			return new CharacterLiteral(stream.expect(TokenType.CharacterLiteral).getSpan());
		} else if (stream.match(TokenType.NullLiteral, false)) {
			return new NullLiteral(stream.expect(TokenType.NullLiteral).getSpan());
		} else {
			Error.error("Expected a variable, field, map, array, function or method call, or literal.", stream);
			return null; 
		}
	}

	private AbstractExpression parseMapLiteral (TokenStream stream) {
		Span openCurly = stream.expect(TokenType.LeftCurly).getSpan();

		List<Span> keys = new ArrayList<>();
		List<AbstractExpression> values = new ArrayList<>();
		while (stream.hasMore() && !stream.match(TokenType.RightCurly, false)) {
			keys.add(stream.expect(TokenType.Identifier).getSpan());
			stream.expect(":");
			values.add(parseExpression(stream));
			if (!stream.match(TokenType.RightCurly, false)) {
				stream.expect(TokenType.Comma);
			}
		}

		Span closeCurly = stream.expect(TokenType.RightCurly).getSpan();
		return new MapLiteral(new Span(openCurly, closeCurly), keys, values);
	}

	private AbstractExpression parseListLiteral (TokenStream stream) {
		Span openBracket = stream.expect(TokenType.LeftBracket).getSpan();

		List<AbstractExpression> values = new ArrayList<>();
		while (stream.hasMore() && !stream.match(TokenType.RightBracket, false)) {
			values.add(parseExpression(stream));
			if (!stream.match(TokenType.RightBracket, false)) {
				stream.expect(TokenType.Comma);
			}
		}

		Span closeBracket = stream.expect(TokenType.RightBracket).getSpan();
		return new ListLiteral(new Span(openBracket, closeBracket), values);
	}

	private AbstractExpression parseAccessOrCall (TokenStream stream) {
		Span identifier = stream.expect(TokenType.Identifier).getSpan();
		AbstractExpression result = new VariableAccess(identifier);

		while (stream.hasMore() && stream.match(false, TokenType.LeftParantheses, TokenType.LeftBracket, TokenType.Period)) {

			
			if (stream.match(TokenType.LeftParantheses, false)) {
				List<AbstractExpression> arguments = parseArguments(stream);
				Span closingSpan = stream.expect(TokenType.RightParantheses).getSpan();
				if (result instanceof VariableAccess || result instanceof MapOrArrayAccess) {
					result = new FunctionCall(new Span(result.getSpan(), closingSpan), result, arguments);
				} else if (result instanceof MemberAccess) {
					result = new MethodCall(new Span(result.getSpan(), closingSpan), (MemberAccess)result, arguments);
				} else {
					Error.error("Expected a variable, field or method.", stream);
				}
			}

			
			else if (stream.match(TokenType.LeftBracket, true)) {
				AbstractExpression keyOrIndex = parseExpression(stream);
				Span closingSpan = stream.expect(TokenType.RightBracket).getSpan();
				result = new MapOrArrayAccess(new Span(result.getSpan(), closingSpan), result, keyOrIndex);
			}

			
			else if (stream.match(TokenType.Period, true)) {
				identifier = stream.expect(TokenType.Identifier).getSpan();
				result = new MemberAccess(result, identifier);
			}
		}

		return result;
	}

	/** Does not consume the closing parentheses. **/
	private List<AbstractExpression> parseArguments (TokenStream stream) {
		stream.expect(TokenType.LeftParantheses);
		List<AbstractExpression> arguments = new ArrayList<>();
		while (stream.hasMore() && !stream.match(TokenType.RightParantheses, false)) {
			arguments.add(parseExpression(stream));
			if (!stream.match(TokenType.RightParantheses, false)) {
				stream.expect(TokenType.Comma);
			}
		}
		return arguments;
	}

	private AbstractNode parseReturn (TokenStream tokens) {
		Span returnSpan = tokens.expect("return").getSpan();
		if (tokens.match(SYMBOL_SEMICOLON, false)) {
			return new Return(returnSpan, null);
		}
		AbstractExpression returnValue = parseExpression(tokens);
		return new Return(new Span(returnSpan, returnValue.getSpan()), returnValue);
	}

	@SuppressWarnings("serial")
	public static class Macros extends HashMap<String, Macro> {
	}

	public static class ParserResult {
		private final List<AbstractNode> nodes;
		private final Macros macros;
		private final List<Include> includes;
		private final List<IncludeRaw> rawIncludes;

		public ParserResult (List<AbstractNode> nodes, Macros macros, List<Include> includes, List<IncludeRaw> rawIncludes) {
			this.nodes = nodes;
			this.macros = macros;
			this.includes = includes;
			this.rawIncludes = rawIncludes;
		}

		public List<AbstractNode> getNodes () {
			return nodes;
		}

		public Macros getMacros () {
			return macros;
		}

		public List<Include> getIncludes () {
			return includes;
		}

		public List<IncludeRaw> getRawIncludes () {
			return rawIncludes;
		}
	}
}
