package com.acknsyn.lox

import java.io.File
import javax.swing.GroupLayout.Group
import kotlin.system.exitProcess
import kotlin.text.Charsets.UTF_8


private val KEYWORDS = mapOf(
    "and" to TokenType.AND,
    "class" to TokenType.CLASS,
    "else" to TokenType.ELSE,
    "false" to TokenType.FALSE,
    "for" to TokenType.FOR,
    "fun" to TokenType.FUN,
    "if" to TokenType.IF,
    "nil" to TokenType.NIL,
    "or" to TokenType.OR,
    "print" to TokenType.PRINT,
    "return" to TokenType.RETURN,
    "super" to TokenType.SUPER,
    "this" to TokenType.THIS,
    "true" to TokenType.TRUE,
    "var" to TokenType.VAR,
    "while" to TokenType.WHILE
)

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens: List<Token> = scanner.scanTokens();

    tokens.forEach {
        println(it)
    }
}

fun runFile(filename: String) {
    val text = File(filename).readText(UTF_8)
    run(text)
}

fun runPrompt() {
    while (true) {
        println("> ")
        try {
            val line = readln()
            run(line)
        } catch (e: Exception) {
            break;
        }
    }
}

enum class TokenType {
    //single char tokens
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    DOT,
    MINUS,
    PLUS,
    SEMICOLON,
    SLASH,
    STAR,

    // one or two char tokens
    BANG,
    BANG_EQUAL,
    EQUAL,
    EQUAL_EQUAL,
    GREATER,
    GREATER_EQUAL,
    LESS,
    LESS_EQUAL,

    //literals
    IDENTIFIER,
    STRING,
    NUMBER,

    //keywords
    AND,
    CLASS,
    ELSE,
    FALSE,
    FUN,
    FOR,
    IF,
    NIL,
    OR,
    PRINT,
    RETURN,
    SUPER,
    THIS,
    TRUE,
    VAR,
    WHILE,
    EOF
}

data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int)

fun error(line: Int, msg: String) {
    report(line, "", msg)
}

fun report(line: Int, where: String, msg: String) {
    System.err.println("[line ${line}] Error ${where}: $msg")
}

class Scanner(private val source: String) {
    private val tokens: MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = start
    private var line = 1

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))

        return tokens
    }

    private fun advance() = source[current++]

    private fun addToken(type: TokenType, literal: Any? = null, lexeme: String = "") =
        tokens.add(Token(type, lexeme, literal, line))

    private fun match(expected: Char): Boolean = when {
        isAtEnd() -> false
        source[current] != expected -> false
        else -> {
            current++
            true
        }
    }

    private fun peek(): Char {
        if (isAtEnd()) {
            return '\u0000'
        }
        return source[current]
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }
                } else if (match('*')) {
                    advance()
                    blockComment()
                } else {
                    addToken(TokenType.SLASH)
                }
            }

            ' ', '\r', '\t' -> {}

            '\n' -> line++

            '"' -> string()

            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun blockComment() {
        if (peek() == '*' && peekNext() == '/') {
            advance()
            advance()
        }

        if (peek() == '/' && peekNext() == '*') {
            advance()
            advance()
            blockComment()
        }

        while (!isAtEnd()) {
            val c = advance()
            if (c == '\n') line++
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val lexeme = source.substring(start, current)
        val keyword = KEYWORDS[lexeme]

        if (keyword != null) {
            addToken(keyword)
        } else {
            addToken(TokenType.IDENTIFIER, lexeme = lexeme)
        }
    }

    private fun isAlphaNumeric(c: Char) = isAlpha(c) || isDigit(c)

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }

        val lexeme = source.substring(start, current)
        val literal = lexeme.toDouble()

        addToken(TokenType.NUMBER, literal = literal)
    }

    private fun isDigit(c: Char) = (c in '0'..'9')

    private fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z') || c == '_';

    private fun peekNext(): Char {
        val nextPos = current + 1

        return if (nextPos >= source.length) {
            '\u0000'
        } else {
            source[nextPos]
        }
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            error(line, "Unterminated string.")
            return
        }

        advance()

        val literal = source.substring(start + 1, current - 1)
        val lexeme = source.substring(start, current)
        addToken(TokenType.STRING, literal, lexeme)
    }
}

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

class AstPrinter : Visitor<String> {
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

class RpnPrinter : Visitor<String> {
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