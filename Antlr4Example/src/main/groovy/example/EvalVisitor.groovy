package example

import java.util.Map;
import java.util.HashMap

class EvalVisitor extends LabeledExprBaseVisitor<Integer> {
	Map<String, Integer> memory = new HashMap<String, Integer>()

	@Override Integer visitAssign(LabeledExprParser.AssignContext ctx) {
		def id = ctx.ID().text
		def value = visit(ctx.expr())
		memory.put(id, value) // returns value, which becomes the return
	}

	@Override Integer visitPrintExpr(LabeledExprParser.PrintExprContext ctx) {
		def value = visit(ctx.expr())
		println value
	}

	@Override Integer visitInt(LabeledExprParser.IntContext ctx) {
		return Integer.valueOf(ctx.INT().text)
	}

	@Override Integer visitId(LabeledExprParser.IdContext ctx) {
		def id = ctx.ID().text
		return memory.get(id) ?: 0
	}

	@Override Integer visitMulDiv(LabeledExprParser.MulDivContext ctx) {
		def left = visit(ctx.expr(0))
		def right = visit(ctx.expr(1))
		return ctx.op.type == LabeledExprParser.MUL ? left * right : left / right
	}

	@Override Integer visitAddSub(LabeledExprParser.AddSubContext ctx) {
		def left = visit(ctx.expr(0))
		def right = visit(ctx.expr(1))
		return ctx.op.type == LabeledExprParser.ADD ? left + right : left - right
	}

	@Override Integer visitParens(LabeledExprParser.ParensContext ctx) {
		return visit(ctx.expr())
	}
}