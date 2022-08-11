package com.acknsyn.lox

class AstPrinter : Expr.Visitor<String> {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val expr = Binary(
                Unary(
                    Token(TokenType.MINUS, "-", null, 1),
                    Literal(123)
                ),
                Token(TokenType.STAR, "*", null, 1),
                Grouping(
                    Literal(45.67)
                )
            )
            println(AstPrinter().print(expr))
        }
    }

    fun print(expr: Expr): String = expr.accept(this)

    override fun visitLiteral(it: Literal): String = when (it.value) {
        null -> "nil"
        else -> it.value.toString()
    }

    override fun visitUnary(it: Unary): String = parenthesize(it.op.lexeme, it.right)

    override fun visitBinary(it: Binary): String = parenthesize(it.op.lexeme, it.left, it.right)

    override fun visitGrouping(it: Grouping): String = parenthesize("group", it.expr)

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        return "(${name}${exprs.map { " " + it.accept(this) }.joinToString(separator = "")})"
    }
}