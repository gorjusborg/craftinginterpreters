package com.acknsyn.lox

class Parser(private val tokens: List<Token>) {
    private var current: Int = 0;

    fun parse(): List<Stmt> = try {
        program()
    } catch (error: ParseError) {
        emptyList()
    }

    private fun program(): List<Stmt> {
        var statements = mutableListOf<Stmt>()
        while(!isAtEnd()) {
            statements.add(statement())
        }
        return statements;
    }

    private fun statement(): Stmt = when {
        match(TokenType.PRINT) -> printStatement()
        else -> expressionStatement()
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Print(value)
    }

    private fun expressionStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Expression(value);
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

    /**
     * Prints and returns a ParseError
     */
    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    /**
     * Advance to next statement-ish.
     * Meant to be called following parse errors to
     * allow parsing past the first error.
     */
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

    /**
     * Checks if token at current position is of type, advances to next token if it is.
     * Otherwise, throws error with provided message.
     */
    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(peek(), message)
    }

    /**
     * Checks if token at current position is any of the provided types.
     * Advances to next position in stream if it is, otherwise does not advance to next token.
     */
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    /**
     * Returns token at current stream, then advances to next token in stream.
     */
    private fun advance(): Token {
        if (!isAtEnd()) {
            current += 1
        }
        return previous()
    }

    /**
     * Checks whether token at current position is of a particular type.
     * Does not advance to next token.
     */
    private fun check(type: TokenType): Boolean = when {
        isAtEnd() -> false
        else -> peek().type == type
    }

    /**
     * Checks whether current token is EOF. Does not advance to next token.
     */
    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    /**
     * Returns token at current position. Does not advance to next token.
     */
    private fun peek(): Token = tokens[current]

    /**
     * Returns token at position before current position
     */
    private fun previous(): Token = tokens[current - 1]
}