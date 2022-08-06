package com.acknsyn.lox

import java.io.File
import kotlin.system.exitProcess
import kotlin.text.Charsets.UTF_8

class Lox {
    companion object {
        private var hadEvalError: Boolean = false

        private val interpreter: Interpreter = Interpreter()

        fun report(line: Int, where: String, msg: String) {
            System.err.println("[line ${line}] Error ${where}: $msg")
        }

        fun error(line: Int, msg: String) {
            report(line, "", msg)
        }

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, "at '${token.lexeme}'", message)
            }
        }

        fun evalError(error: EvalError) {
            error(error.token, error.message ?: "unknown error")
            hadEvalError = true
        }

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size > 1) {
                println("Usage: klox [script]")
                exitProcess(64)
            } else if (args.size == 1) {
                runFile(args[0])
                if (hadEvalError) {
                    exitProcess(70)
                }
            } else {
                runPrompt()
            }
        }

        private fun run(source: String) {
            val scanner = Scanner(source)
            val tokens: List<Token> = scanner.scanTokens();
            val parser = Parser(tokens)
            val expr = parser.parse()

            expr?.let { interpreter.interpret(it) }
        }

        private fun runFile(filename: String) {
            val text = File(filename).readText(UTF_8)
            run(text)
        }

        private fun runPrompt() {
            while (true) {
                print("> ")
                try {
                    val line = readln()
                    run(line)
                } catch (e: Exception) {
                    break;
                }
            }
        }
    }
}