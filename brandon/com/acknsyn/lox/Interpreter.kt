package com.acknsyn.lox

class Interpreter: Visitor<Any?> {
    override fun visitLiteral(it: Literal): Any? = it.value;

    override fun visitUnary(it: Unary): Any? {
        val value = eval(it.right)

        return when (it.op.type) {
            TokenType.MINUS -> -(value as Double)
            TokenType.BANG -> !(value as Boolean)
            else -> throw EvalError(it.op, "invalid operator")
        }
    }

    override fun visitBinary(it: Binary): Any? {
        val leftVal = eval(it.left)
        val rightVal = eval(it.right)

        return when (it.op.type) {
            TokenType.PLUS -> when {
                    leftVal is Double && rightVal is Double -> leftVal + rightVal
                    leftVal is String || rightVal is String -> leftVal.toString() + rightVal.toString()
                    else -> throw EvalError(it.op, "invalid attempt at addition")
                }
            TokenType.MINUS -> when {
                leftVal is Double && rightVal is Double -> leftVal - rightVal
                else -> throw EvalError(it.op, "invalid attempt at subtraction")
            }
            TokenType.STAR -> when {
                leftVal is Double && rightVal is Double -> leftVal * rightVal
                else -> throw EvalError(it.op, "invalid attempt at multiplication")
            }
            TokenType.SLASH -> when {
                leftVal is Double && rightVal is Double -> leftVal / rightVal
                else -> throw EvalError(it.op, "invalid attempt at division")
            }
            TokenType.GREATER -> when {
                leftVal is Double && rightVal is Double -> leftVal > rightVal
                else -> throw EvalError(it.op, "invalid attempt at gt")
            }
            TokenType.GREATER_EQUAL -> when {
                leftVal is Double && rightVal is Double -> leftVal >= rightVal
                else -> throw EvalError(it.op, "invalid attempt at gte")
            }
            TokenType.LESS -> when {
                leftVal is Double && rightVal is Double -> leftVal < rightVal
                else -> throw EvalError(it.op, "invalid attempt at lt")
            }
            TokenType.LESS_EQUAL -> when {
                leftVal is Double && rightVal is Double -> leftVal <= rightVal
                else -> throw EvalError(it.op, "invalid attempt at lte")
            }
            TokenType.EQUAL -> isEqual(rightVal, leftVal)
            TokenType.BANG_EQUAL -> !isEqual(rightVal, leftVal)
            else -> throw EvalError(it.op, "invalid binary operation")
        }
    }

    override fun visitGrouping(it: Grouping): Any?  = eval(it.expr)

    private fun eval(expr: Expr) = expr.accept(this)

    private fun isEqual(left: Any?, right: Any?) = when {
        left == null && right == null -> true
        left == null -> false
        else -> left == right
    }

}