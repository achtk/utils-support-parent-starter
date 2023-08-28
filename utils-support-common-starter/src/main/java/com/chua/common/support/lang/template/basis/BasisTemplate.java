
package com.chua.common.support.lang.template.basis;

import com.chua.common.support.lang.template.basis.interpreter.AstInterpreter;
import com.chua.common.support.lang.template.basis.parsing.Ast;
import com.chua.common.support.lang.template.basis.parsing.Ast.AbstractNode;
import com.chua.common.support.lang.template.basis.parsing.Ast.Include;
import com.chua.common.support.lang.template.basis.parsing.Ast.Macro;
import com.chua.common.support.lang.template.basis.parsing.Parser;
import com.chua.common.support.lang.template.basis.parsing.Parser.Macros;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** A template is loaded by a {@link TemplateLoader} from a file marked up with the basis-template language. The template can be
 * rendered to a {@link String} or {@link OutputStream} by calling one of the <code>render()</code> methods. The
 * {@link TemplateContext} passed to the <code>render()</code> methods is used to look up variable values referenced in the
 * template.
 * @author Administrator
 * */
public class BasisTemplate{
	private final List<AbstractNode> nodes;
	private final Macros macros;
	private final List<Include> includes;

	/** Internal. Created by {@link Parser}. **/
	public BasisTemplate(List<AbstractNode> nodes, Macros macros, List<Include> includes) {
		this.nodes = nodes;
		this.macros = macros;
		this.includes = includes;

		for (Macro macro : macros.values()) {
			macro.setTemplate(this);
		}
	}

	/** Internal. The AST nodes representing this template after parsing. See {@link Ast}. Used by {@link AstInterpreter}. **/
	public List<AbstractNode> getNodes () {
		return nodes;
	}

	/** Internal. The top-level macros defined in the template. See {@link Macro}. Used by the {@link AstInterpreter}. **/
	public Macros getMacros () {
		return macros;
	}

	/** Internal. The includes referenced in this template. A {@link TemplateLoader} is responsible for setting the template
	 * instances referenced by includes. **/
	public List<Include> getIncludes () {
		return includes;
	}

	/** Renders the template using the TemplateContext to resolve variable values referenced in the template. **/
	public String render (TemplateContext context) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(2 * 1024);
		render(context, out);
		try {
			out.close();
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			Error.error("Couldn't render template to string, " + e.getMessage(), nodes.get(0).getSpan());
			return null;
		}
	}

	/** Renderes the template to the OutputStream as UTF-8, using the TemplateContext to resolve variable values referenced in the
	 * template. If a return statement with a return value was encountered, the method returns that return value. Otherwise null is
	 * returned. **/
	public Object render (TemplateContext context, OutputStream out) {
		return AstInterpreter.interpret(this, context, out);
	}

	/** Evaluates this template using the TemplateContext to resolve variable values referenced in the template. Returns any value
	 * returned by the template, or null. **/
	public Object evaluate (TemplateContext context) {
		return AstInterpreter.interpret(this, context, new OutputStream() {
			@Override
			public void write (int b) throws IOException {
			}
		});
	}


}
