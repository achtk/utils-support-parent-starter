
package com.chua.common.support.lang.template.basis.interpreter;

import com.chua.common.support.lang.template.basis.BasisTemplate;
import com.chua.common.support.lang.template.basis.Error.TemplateException;
import com.chua.common.support.lang.template.basis.TemplateContext;
import com.chua.common.support.lang.template.basis.parsing.Ast;
import com.chua.common.support.lang.template.basis.parsing.Ast.Break;
import com.chua.common.support.lang.template.basis.parsing.Ast.Continue;
import com.chua.common.support.lang.template.basis.parsing.Ast.Return;
import com.chua.common.support.lang.template.basis.parsing.Ast.Return.ReturnValue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>
 * Interprets a Template given a TemplateContext to lookup variable values in and writes the evaluation results to an output
 * stream. Uses the global {@link BaseReflection} instance as returned by {@link BaseReflection#getInstance()} to access members and call
 * methods.
 * </p>
 *
 * <p>
 * The interpeter traverses the AST as stored in {@link BasisTemplate#getNodes()}. the interpeter has a method for each AST node type
 * (see {@link Ast} that evaluates that node. A node may return a value, to be used in the interpretation of a parent node or to
 * be written to the output stream.
 * </p>
 * @author Administrator
 **/
public class AstInterpreter {
	public static Object interpret (BasisTemplate template, TemplateContext context, OutputStream out) {
		try {
			Object result = interpretNodeList(template.getNodes(), template, context, out);
			if (result == Return.RETURN_SENTINEL) {
				return ((ReturnValue)result).getValue();
			} else {
				return null;
			}
		} catch (Throwable t) {
			if (t instanceof TemplateException) {
				throw (TemplateException)t;
			} else {
				com.chua.common.support.lang.template.basis.Error.error("Couldn't interpret node list due to I/O error, " + t.getMessage(), template.getNodes().get(0).getSpan());
				return null;
			}
		} finally {
			// clear out RETURN_SENTINEL as it uses a ThreadLocal and would leak memory otherwise
			Return.RETURN_SENTINEL.setValue(null);
		}
	}

	public static Object interpretNodeList (List<Ast.AbstractNode> nodes, BasisTemplate template, TemplateContext context, OutputStream out) throws IOException {
		for (int i = 0, n = nodes.size(); i < n; i++) {
			Ast.AbstractNode node = nodes.get(i);
			Object value = node.evaluate(template, context, out);
			if (value != null) {
				if (value == Break.BREAK_SENTINEL || value == Continue.CONTINUE_SENTINEL || value == Return.RETURN_SENTINEL) {
					return value;
				} else {
					out.write(value.toString().getBytes(UTF_8));
				}
			}
		}
		return null;
	}
}
