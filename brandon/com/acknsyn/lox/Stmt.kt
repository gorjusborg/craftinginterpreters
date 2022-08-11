package com.acknsyn.lox

abstract class Stmt {
    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visitExpressionStmt(it: Expression): R
        fun visitPrintStmt(it: Print): R
    }
}

class Expression(val expr: Expr): Stmt() {
    override fun <R> accept(visitor: Visitor<R>): R = visitor.visitExpressionStmt(this)
}

class Print(val expr: Expr): Stmt() {
    override fun <R> accept(visitor: Visitor<R>): R = visitor.visitPrintStmt(this)
}
