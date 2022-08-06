package com.acknsyn.lox

class EvalError(val token: Token, message: String?) : RuntimeException("$message at line ${token.line}")