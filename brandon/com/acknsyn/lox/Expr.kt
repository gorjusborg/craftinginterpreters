package com.acknsyn.lox

abstract class Expr {
    abstract fun <R> accept(visitor: Visitor<R>): R
}

interface Visitor<R> {
    fun visitLiteral(it: Literal): R
    fun visitUnary(it: Unary): R
    fun visitBinary(it: Binary): R
    fun visitGrouping(it: Grouping): R
}

class Literal(val value: Any?) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitLiteral(this)
}

class Unary(val op: Token, val right: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>): R = visitor.visitUnary(this)
}

class Binary(val left: Expr, val op: Token, val right: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBinary(this)

}

class Grouping(val expr: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGrouping(this)
}