package com.acknsyn.lox

enum class TokenType(val lexeme: String = "") {
    //single char tokens
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    COMMA(","),
    DOT("."),
    MINUS("-"),
    PLUS("+"),
    SEMICOLON(";"),
    SLASH("/"),
    STAR("*"),

    // one or two char tokens
    BANG("!"),
    BANG_EQUAL("!="),
    EQUAL("="),
    EQUAL_EQUAL("=="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),

    //literals
    IDENTIFIER,
    STRING,
    NUMBER,

    //keywords
    AND("and"),
    CLASS("class"),
    ELSE("else"),
    FALSE("false"),
    FUN("fun"),
    FOR("for"),
    IF("if"),
    NIL("nil"),
    OR("or"),
    PRINT("print"),
    RETURN("return"),
    SUPER("super"),
    THIS("this"),
    TRUE("true"),
    VAR("var"),
    WHILE("while"),
    EOF;
}