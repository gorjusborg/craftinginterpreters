package com.acknsyn.lox

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

    private fun addToken(type: TokenType, literal: Any? = null, lexeme: String? = null) {
        val lex = when {
            lexeme != null -> lexeme
            else -> type.lexeme
        }
        tokens.add(Token(type, lex, literal, line))
    }

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
                    Lox.error(line, "Unexpected character.")
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
            Lox.error(line, "Unterminated string.")
            return
        }

        advance()

        val literal = source.substring(start + 1, current - 1)
        val lexeme = source.substring(start, current)
        addToken(TokenType.STRING, literal, lexeme)
    }
}