package example

import org.antlr.v4.runtime.*

class Calc {
	static main(args) {
		def is
		if (args.size() > 0 && args[0] != null) {
			is = new FileInputStream(args[0])
		} else {
			is = System.in
		}
		def input = new ANTLRInputStream(is)
		def lexer = new LabeledExprLexer(input)
		def tokens = new CommonTokenStream(lexer);
		def parser = new LabeledExprParser(tokens);
		def tree = parser.prog();

		def eval = new EvalVisitor();
		eval.visit(tree);
	}
}