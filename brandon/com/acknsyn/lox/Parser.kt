package com.acknsyn.lox

class Parser(private val tokens: List<Token>) {
    private var current: Int = 0;

    fun parse(): Expr? = try {
        expression()
    } catch (error: ParseError) {
        null
    }

    private fun expression(): Expr = equality()
    private fun equality(): Expr {
        var expr = comparison()

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val op = previous()
            val right = comparison()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val op = previous()
            val right = term()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while(match(TokenType.MINUS, TokenType.PLUS)) {
            val op = previous()
            val right = factor()
            expr = Binary(expr, op, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while(match(TokenType.SLASH, TokenType.STAR)) {
            val op = previous()
            val right = unary()
            expr = Binary(expr, op, right)
        }

        return expr
    }

    private fun unary(): Expr {
        while (match(TokenType.BANG, TokenType.MINUS)) {
            val op = previous()
            val right = unary()
            return Unary(op, right)
        }

        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.TRUE, TokenType.FALSE, TokenType.NIL)) {
            return Literal(previous().literal)
        }
        return grouping()
    }
    private fun grouping(): Expr {
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Grouping(expr)
        }
        throw error(peek(), "Expect expression")
    }

    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    private fun synchronize() {
        advance()

        while(!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.CLASS,
                TokenType.FOR,
                TokenType.FUN,
                TokenType.IF,
                TokenType.PRINT,
                TokenType.RETURN,
                TokenType.VAR,
                TokenType.WHILE -> return
                else -> advance()
            }
        }
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(peek(), message)
    }
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            current += 1
        }
        return previous()
    }

    private fun check(type: TokenType): Boolean = when {
        isAtEnd() -> false
        else -> peek().type == type
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]
}