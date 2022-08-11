package com.acknsyn.lox

class RpnPrinter : Expr.Visitor<String> {
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
            println(RpnPrinter().print(expr))
        }
    }

    fun print(expr: Expr): String = expr.accept(this)

    override fun visitLiteral(it: Literal): String  = when (it.value) {
        null -> "nil"
        else -> it.value.toString()
    }

    override fun visitUnary(it: Unary): String = "${it.right.accept(this)} ${it.op.lexeme}"

    override fun visitBinary(it: Binary): String = "${it.left.accept(this)} ${it.right.accept(this)} ${it.op.lexeme}"

    override fun visitGrouping(it: Grouping): String = it.expr.accept(this)
}