package com.acknsyn.lox

class EvalError(token: Token, message: String?) : RuntimeException("$message at line ${token.line}")